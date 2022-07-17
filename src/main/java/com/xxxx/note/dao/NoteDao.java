package com.xxxx.note.dao;

import cn.hutool.core.util.StrUtil;
import com.xxxx.note.po.Note;
import com.xxxx.note.po.NoteType;
import com.xxxx.note.vo.NoteVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加或修改云记,返回受影响的行数
 */
public class NoteDao {
    public int addOrUpdate(Note note) {
        String sql = "";
        List<Object> params = new ArrayList<>();

        params.add(note.getTypeId());
        params.add(note.getTitle());
        params.add(note.getContent());

        //判断noteId是否为空，如果为空，则为添加操作，不为空，则修改操作
        if (note.getNoteId() == null){
            sql = "insert into tb_note (typeId,title,content,pubTime,lon,lat) values(?,?,?,now(),?,?)";
            params.add(note.getLon());
            params.add(note.getLat());
        }else {
            sql = "update tb_note set typeId = ?, title = ?, content = ? where noteId = ?";
            params.add(note.getNoteId());
        }

        int row = BaseDao.executeUpdate(sql,params);

        return row;
    }

    /**
     * 查询当前登录用户的云记数量，返回总记录数
     * @param userId
     * @return
     */
    public long findNoteCount(Integer userId,String title,String date,String typeId) {
        String sql = "select count(1) from tb_note n inner join tb_note_type t on n.typeId = t.typeId where userId = ?";

        List<Object> params = new ArrayList<>();
        params.add(userId);

        //如果参数不为空，则拼接sql语句，并设置所需的参数
        if (!StrUtil.isBlank(title)){
            sql += " and title like concat('%',?,'%') ";
            params.add(title);
        }else if (!StrUtil.isBlank(date)){//日期
            sql += " and date_format(pubTime,'%Y年%m月') = ?";
            params.add(date);
        }else if (!StrUtil.isBlank(typeId)){//类型
            sql += " and n.typeId = ?";
            params.add(typeId);
        }

        //调用BaseDao的查询方法
        long count = (long) BaseDao.findSingleValue(sql,params);
        return count;
    }

    /**
     *  查询当前登录用户下当前页的数据列表，返回note集合
     * @param userId
     * @param index
     * @param pageSize
     * @return
     */
    public List<Note> findNoteListByPage(Integer userId, Integer index, Integer pageSize,String title,String date,String typeId) {
        String sql = "select noteId,pubTime,title from tb_note n inner join tb_note_type t on n.typeId = t.typeId where userId = ? ";

        List<Object> params = new ArrayList<>();
        params.add(userId);

        //如果参数不为空，则拼接sql语句，并设置所需的参数
        if (!StrUtil.isBlank(title)){
            sql += " and title like concat('%',?,'%')";
            params.add(title);
        }else if (!StrUtil.isBlank(date)){//日期
            sql += " and date_format(pubTime,'%Y年%m月') = ?";
            params.add(date);
        }else if (!StrUtil.isBlank(typeId)){//类型
            sql += " and n.typeId = ?";
            params.add(typeId);
        }

        //拼接分页的sql语句（limit语句要写道sql语句最后）
        sql += " order by pubTime desc limit ?,?";

        params.add(index);
        params.add(pageSize);

        List<Note> noteList = BaseDao.queryRows(sql,params,Note.class);

        return noteList;
    }

    /**
     * 通过日期分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByDate(Integer userId) {
        //定义sql语句
        String sql = "SELECT COUNT(1) noteCount,DATE_FORMAT(pubTime,'%Y年%m月') groupName  FROM tb_note n \n" +
                " INNER JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ?\n" +
                " GROUP BY DATE_FORMAT(pubTime,'%Y年%m月')\n" +
                " ORDER BY DATE_FORMAT(pubTime,'%Y年%m月') DESC";
        List<Object> params = new ArrayList<>();
        params.add(userId);

        List<NoteVo> list = BaseDao.queryRows(sql,params,NoteVo.class);

        return list;

    }

    public List<NoteVo> findNoteCountByType(Integer userId) {
        String sql = "SELECT COUNT(noteId) noteCount, t.typeId , typeName groupName FROM tb_note n " +
                " RIGHT JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ?" +
                " GROUP BY t.typeId ORDER BY COUNT(noteId) DESC";

        List<Object> params = new ArrayList<>();
        params.add(userId);

        List<NoteVo> list = BaseDao.queryRows(sql,params,NoteVo.class);

        return list;
    }

    /**
     * 通过Id查询云记对象
     * @param noteId
     * @return
     */
    public Note findNoteById(String noteId) {
        String sql = "select noteId,title,content,pubTime,typeName,n.typeId from tb_note n inner join" +
                " tb_note_type t on n.typeId=t.typeId  where noteId = ?";

        List<Object> params = new ArrayList<>();
        params.add(noteId);

        Note note = (Note) BaseDao.queryRow(sql,params,Note.class);

        return note;
    }

    /**
     * 通过noteId删除云记记录，返回受影响的行数
     * @param noteId
     * @return
     */
    public int deleteNoteById(String noteId) {
        String sql = "delete from tb_note where noteId = ?";

        List<Object> params = new ArrayList<>();
        params.add(noteId);
        int row = BaseDao.executeUpdate(sql,params);

        return row;
    }

    /**
     * 查询发布云记时的坐标
     * @param userId
     * @return
     */
    public List<Note> queryNoteList(Integer userId) {
        String sql = "select lon,lat from tb_note n inner join tb_note_type t on n.typeId = t.typeId where userId = ?";

        List<Object> params = new ArrayList<>();
        params.add(userId);

        List<Note> list = BaseDao.queryRows(sql,params,Note.class);

        return list;

    }
}
