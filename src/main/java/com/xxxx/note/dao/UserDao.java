package com.xxxx.note.dao;

import com.xxxx.note.po.User;
import com.xxxx.note.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    /**
     * 通过用户名查询
     * @param userName
     * @return
     */
    public User queryUserByName(String userName){
        User user = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = DBUtil.getConnetion();
            String sql = "select * from tb_user where uname = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                user = new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setUname(userName);
                user.setHead(resultSet.getString("head"));
                user.setMood(resultSet.getString("mood"));
                user.setNick(resultSet.getString("nick"));
                user.setUpwd(resultSet.getString("upwd"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭资源
            DBUtil.close(resultSet,preparedStatement,connection);
        }

        return user;
    }

    public User queryUserByName2(String userName){

        String sql = "select * from tb_user where uname = ?";

        List<Object> params = new ArrayList<>();
        params.add(userName);
        User user = (User) BaseDao.queryRow(sql,params,User.class);
        return user;
    }

    /**
     * 通过昵称与用户id查询对象
     * @param nick
     * @param userId
     * @return
     */
    public User queryByNickAndUserId(String nick, Integer userId) {
        String sql = "select * from tb_user where nick = ? and userId != ?";
        List<Object> params = new ArrayList<>();
        params.add(nick);
        params.add(userId);
        User user = (User) BaseDao.queryRow(sql,params,User.class);
        return user;
    }

    /**
     * 通过用户ID修改用户信息
     * @param user
     * @return
     */
    public int updateUser(User user) {
        String sql = "update tb_user set nick = ?,mood = ? , head = ? where userId = ?";
        List<Object> parans = new ArrayList<>();
        parans.add(user.getNick());
        parans.add(user.getMood());
        parans.add(user.getHead());
        parans.add(user.getUserId());

        int row = BaseDao.executeUpdate(sql,parans);
        return row;
    }
}
