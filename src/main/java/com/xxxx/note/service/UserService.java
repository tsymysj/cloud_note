package com.xxxx.note.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.xxxx.note.dao.UserDao;
import com.xxxx.note.po.User;
import com.xxxx.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;

public class UserService {

    private UserDao userDao = new UserDao();

    /**
     * 用户登录
     *
     * 1.判断参数是否为空
     *     如果为空
     *         设置ResultInfo对象的状态码和提示信息
     *         返回ResultInfo对象
     * 2.如果不为空，通过用户名查询用户对象
     * 3.判断用户对象是否为空
     *      如果为空
     *          设置ResultInfo对象的状态码和提示信息
     *          返回ResultInfo对象
     * 4.如果用户对象不为空，将数据库中查询到的用户对象的密码与前台传递的密码作比较(将密码加密后再进行比较)
     *      如果密码不正确
     *      设置ResultInfo对象的状态码和提示信息
     *         return
     *      如果密码正确
     *        设置ResultInfo对象的状态码和提示信息
     * 5.返回ResultInfo对象
     * @param userName
     * @param userPwd
     * @return
     */
    public ResultInfo<User> userLogin(String userName, String userPwd) {
        ResultInfo<User> resultInfo = new ResultInfo<>();

        //数据回显：当登录失败是，将登陆信息返回个页面显示
        User u = new User();
        u.setUname(userName);
        u.setUpwd(userPwd);
        resultInfo.setResult(u);

        if (StrUtil.isBlank(userName) || StrUtil.isBlank(userPwd)){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户姓名或密码不能为空!");
            return resultInfo;
        }

        User user = userDao.queryUserByName(userName);

        if (user == null){
            resultInfo.setCode(0);
            resultInfo.setMsg("该用户不存在!");
            return resultInfo;
        }

        //将前台传递的密码按照MD5算法的方式加密
        userPwd = DigestUtil.md5Hex(userPwd);
        //判断加密后的密码是否与数据库中的一致
        if (!userPwd.equals(user.getUpwd())){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户密码不正确!");
            return resultInfo;
        }

        resultInfo.setCode(1);
        resultInfo.setResult(user);

        return resultInfo;
    }

    /**
     * 验证昵称的唯一性
     * @param nick
     * @param userId
     * @return
     */
    public Integer checkNick(String nick, Integer userId) {
        if (StrUtil.isBlank(nick)){
            return 0;
        }
        User user = userDao.queryByNickAndUserId(nick,userId);
        if (user != null){
            return 0;
        }
        return 1;
    }

    /**
     * 修改用户信息
     * @param req
     * @return
     */
    public ResultInfo<User> updateUser(HttpServletRequest req) {
        ResultInfo<User> resultInfo = new ResultInfo<>();
        String nick = req.getParameter("nick");
        String mood = req.getParameter("mood");
        if (StrUtil.isBlank(nick)){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户昵称不能为空！");
            return resultInfo;
        }

        User user = (User) req.getSession().getAttribute("user");
        //设置修改的昵称和头像
        user.setNick(nick);
        user.setMood(mood);

        try {
            Part part = req.getPart("img");
            String header = part.getHeader("Content-Disposition");
            //获取具体的请求头对应的值
            String str = header.substring(header.lastIndexOf("=")+2);
            String fileName = str.substring(0,str.length()-1);
            if (!StrUtil.isBlank(fileName)){
                user.setHead(fileName);
                String filePath = req.getServletContext().getRealPath("/WEB-INF/upload/");
                part.write(filePath+"/"+fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        //调用dao
        int row = userDao.updateUser(user);

        if (row > 0){
            resultInfo.setCode(1);
            //更新session用户对象
            req.getSession().setAttribute("user",user);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("更新失败");
        }

        return resultInfo;
    }
}
