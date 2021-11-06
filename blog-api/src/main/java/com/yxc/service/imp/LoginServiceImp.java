package com.yxc.service.imp;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yxc.dao.pojo.SysUser;
import com.yxc.service.LoginService;
import com.yxc.service.SysUserService;
import com.yxc.utils.JWTUtils;
import com.yxc.vo.ErrorCode;
import com.yxc.vo.Result;
import com.yxc.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

@Service
//一个事务控制，也可以放在接口类，如果有多个实现类的话
//用处主要是防止出现业务逻辑出现异常，比如出现异常还继续对数据库进行操作
@Transactional
public class LoginServiceImp implements LoginService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RedisTemplate redisTemplate;

    //加密盐
    private static final String salt = "mszlu!@#";

    //对前端传来的Token进行检验
    @Override
    public SysUser checkToken(String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }

        //解析Token
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if(stringObjectMap == null){
            return null;
        }

        //解析成功，查询是否和redis中存储的一致
        String json = (String)redisTemplate.opsForValue().get("TOKEN_" + token);
        if(StringUtils.isBlank(json)){
            //过期了，只存一天的时间
            return null;
        }

        //前面都通过代表token是正常的
        //把通过token查到的对象转换成实体类对象
        SysUser sysUser = com.alibaba.fastjson.JSON.parseObject(json, SysUser.class);
        return sysUser;
    }

    @Override
    public Result login(LoginParam loginParam) {
        /*
            验证的过程：1. 检查参数是否合法
                      2. 根据用户名和密码去user表中查询 是否存在
                      3. 如果不存在 登陆失败
                      4. 如果存在 ， 使用jwt 生成的 token 返回给前端
                      5. token放入redis中，redis token： user信息  设置token过期时间
                      （登录认证的时候，先认证token字符串是否合法，然后再去redis认证是否存在）
         */
        String account = loginParam.getAccount();

        String password = loginParam.getPassword();

        // 1.
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            //                           失败的状态码                          状态信息
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }

        //数据库中的密码是进行过加密的
        //采用的是md5+加密盐的形式，所以要对从前端获取的密码进行加密
        //（是apache的包）
        password = DigestUtils.md5Hex(password + salt);

        // 2. 3.
        SysUser sysUser = sysUserService.findUser(account, password);
        if(sysUser == null){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode() , ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }


        //4.
        String token = JWTUtils.createToken(sysUser.getId());


        //5. Redis 的操作
        //创建String类型的操作对象
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //              存入       键                                      值                        存活时间       时间单位
        valueOperations.set("TOKEN_"+token , com.alibaba.fastjson.JSON.toJSONString(sysUser) , 1 , TimeUnit.DAYS);

        return Result.success(token);
    }




    /**
     * 退出登录(直接删除掉redis的token就行了)
     * @param token
     * @return
     */
    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token);

        return Result.success(null);
    }

    @Override
    public Result register(LoginParam loginParam) {
        /**  注册的验证流程
         * 1.判断参数 是否合法
         * 2.判断账户是否存在，存在 返回账户已经被注册
         * 3.不存在就注册
         * 4.生成token
         * 5.存入redis并返回
         * 6.注意 加上事务，一旦中间的任何过程出现问题，注册的用户 需要回滚
         */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();

        //1.
        if(StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(nickname)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }

        //2.
        SysUser sysUser = sysUserService.findUserByAccount(loginParam.getAccount());
        if(sysUser != null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(), ErrorCode.ACCOUNT_EXIST.getMsg());
        }

        //3.
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+salt));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        this.sysUserService.save(sysUser);

        //4.
        String token = JWTUtils.createToken(sysUser.getId());


        //5.
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("TOKEN_"+ token , com.alibaba.fastjson.JSON.toJSONString(sysUser) , 1 , TimeUnit.DAYS);
        return Result.success(token);

    }
}









