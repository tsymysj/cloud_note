package com.xxxx.note.dao;

import com.xxxx.note.po.NoteType;
import com.xxxx.note.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteTypeDao {

    /**
     * 通过用户ID查询类型集合
     * @param userId
     * @return
     */
    public List<NoteType> findTypeListByUserId(Integer userId){
        String sql = "select typeId,typeName,userId from tb_note_type where userId = ?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<NoteType> list = BaseDao.queryRows(sql,params,NoteType.class);
        return list;
    }

    /**
     *通过类型ID删除
     * @param typeId
     * @return
     */
    public int deleteTypeById(String typeId) {
        String sql = "delete from tb_note_type where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }

    /**
     * 通过类型ID查询云记记录的数量，返回云记数量
     * @param typeId
     * @return
     */
    public long findNoteCountBuTypeId(String typeId) {
        String sql = "select count(1) from tb_note where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        long count = (long) BaseDao.findSingleValue(sql,params);
        return count;
    }

    /**
     *  查询当前登录用户下，类型名称是否唯一
     *      返回1，成功
     *      返回0，失败
     * @param typeName
     * @param userId
     * @param typeId
     * @return
     */
    public Integer checkTypeName(String typeName, Integer userId, String typeId) {
        String sql = "select * from tb_note_type where userId = ? and typeName = ?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(typeName);
        NoteType noteType = (NoteType) BaseDao.queryRow(sql,params,NoteType.class);
        //如果对象为空，表示可用
        if (noteType == null){
            return 1;
        }else {
            if (typeId.equals(noteType.getTypeId().toString())){
                return 1;
            }
        }
        return 0;
    }

    /**
     * 添加操作，返回主键
     * @param typeName
     * @param userId
     * @return
     */
    public Integer addType(String typeName, Integer userId) {
        Integer key = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

         try {
             connection = DBUtil.getConnetion();
             String sql = "insert into tb_note_type (typeName,userId) values (?,?)";
             preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
             preparedStatement.setString(1,typeName);
             preparedStatement.setInt(2,userId);
             int row = preparedStatement.executeUpdate();
             if (row > 0){
                 //获取返回主键的结果集
                 resultSet = preparedStatement.getGeneratedKeys();
                 //得到主键的值
                 if (resultSet.next()){
                     key = resultSet.getInt(1);
                 }
             }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
             DBUtil.close(resultSet,preparedStatement,connection);
         }
         return key;
    }

    /**
     * 更新修改操作
     * @param typeName
     * @param typeId
     * @return
     */
    public Integer updateType(String typeName, String typeId) {
        String sql = "update tb_note_type set typeName = ? where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeName);
        params.add(typeId);
        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }
}
