package com.xxxx.note.service;

import cn.hutool.core.util.StrUtil;
import com.xxxx.note.dao.NoteDao;
import com.xxxx.note.po.Note;
import com.xxxx.note.util.Page;
import com.xxxx.note.vo.NoteVo;
import com.xxxx.note.vo.ResultInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 添加或修改云记
 */
public class NoteService {

    private NoteDao noteDao = new NoteDao();

    public ResultInfo<Note> addOrUpdate(String typeId, String title, String content,String noteId,String lon,String lat) {
        ResultInfo<Note> resultInfo = new ResultInfo<>();

        if (StrUtil.isBlank(typeId)){
            resultInfo.setCode(0);
            resultInfo.setMsg("请选择云记类型!");
            return resultInfo;
        }
        if (StrUtil.isBlank(title)){
            resultInfo.setCode(0);
            resultInfo.setMsg("云记标题不能为空!");
            return resultInfo;
        }
        if (StrUtil.isBlank(content)){
            resultInfo.setCode(0);
            resultInfo.setMsg("云记内容不能为空!");
            return resultInfo;
        }

        //设置经纬度的默认值，默认设置为北京
        if (lon == null || lat == null){
            lon = "116.404";
            lat = "39.915";
        }


        //设置回显对象
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTypeId(Integer.parseInt(typeId));
        resultInfo.setResult(note);
        note.setLon(Float.parseFloat(lon));
        note.setLat(Float.parseFloat(lat));

        //判断云记ID是否为空
        if (!StrUtil.isBlank(noteId)){
            note.setNoteId(Integer.parseInt(noteId));
        }

        //调用dao
        int row = noteDao.addOrUpdate(note);

        if (row > 0){
            resultInfo.setCode(1);
        }else {

            resultInfo.setCode(0);
            resultInfo.setResult(note);
            resultInfo.setMsg("更新失败");
        }

        return resultInfo;
    }

    /**
     * 分页查询云记列表
     *
     *             1.参数的非空校验
     *                 如果分页参数为空，则设置默认值
     *             2.查询当前登录用户的云记数量，返回总记录数（long类型）
     *             3.判断总记录数是否大于0
     *             4.如果总记录数大于0，调用page类的带参构造，得到其他的分页参数
     *             5.查询当前登录用户下当前页的数据列表，返回note集合
     *             6.将note集合设置到page对象中
     *             7.返回page对象
     * @param pageNumStr
     * @param pageSizeStr
     * @param userId
     * @param title 条件查询的参数
     * @return
     */
    public Page<Note> findNoteListByPage(String pageNumStr, String pageSizeStr, Integer userId,String title,String date,String typeId) {

        Integer pageNum = 1;//默认当前第一页
        Integer pageSize = 5;//默认每页显示5条数据

        //如果参数不为空，设置参数
        if (!StrUtil.isBlank(pageNumStr)){
            pageNum = Integer.parseInt(pageNumStr);
        }
        if (!StrUtil.isBlank(pageSizeStr)){
            pageSize = Integer.parseInt(pageSizeStr);
        }

        long count = noteDao.findNoteCount(userId,title,date,typeId);

        if (count < 1){
            return  null;
        }

        Page<Note> page = new Page<>(pageNum,pageSize,count);

        //得到数据库中分页查询的开始下标
        Integer index = (pageNum - 1) * pageSize;
        List<Note> noteList = noteDao.findNoteListByPage(userId,index,pageSize,title,date,typeId);

        page.setDataList(noteList);

        return page;
    }

    /**
     * 通过日期分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByDate(Integer userId) {
        return noteDao.findNoteCountByDate(userId);
    }

    /**
     * 通过类型分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByType(Integer userId) {
        return noteDao.findNoteCountByType(userId);
    }

    /**
     * 查询云记详情
     * @param noteId
     * @return
     */
    public Note findNoteById(String noteId) {
        if (StrUtil.isBlank(noteId)){
            return null;
        }
        Note note = noteDao.findNoteById(noteId);

        return note;

    }

    /**
     * 删除云记
     * @param noteId
     * @return
     */
    public Integer deleteNote(String noteId) {
        if (StrUtil.isBlank(noteId)){
            return 0;
        }
        int row = noteDao.deleteNoteById(noteId);
        if (row > 0){
            return  1;
        }
        return 0;
    }


    /**
     * 通过月份查询对应的云记数量
     * @param userId
     * @return
     */
    public ResultInfo<Map<String, Object>> queryNoteCountByMonth(Integer userId) {
        ResultInfo<Map<String,Object>> resultInfo = new ResultInfo<>();

        //通过月份分类查询云记数量
        List<NoteVo> noteVos = noteDao.findNoteCountByDate(userId);

        //判断集合是否存在
        if (noteVos != null && noteVos.size() > 0 ){
            //得到月份
            List<String> monthList = new ArrayList<>();
            //得到云集集合
            List<Integer> noteCountList = new ArrayList<>();

            //遍历月份分组
            for (NoteVo noteVo:noteVos){
                monthList.add(noteVo.getGroupName());
                noteCountList.add((int) noteVo.getNoteCount());
            }
            //准备map对象，封装对应的月份与云集数量
            Map<String,Object> map = new HashMap<>();
            map.put("monthArray",monthList);
            map.put("dataArray",noteCountList);

            //将map对象设置到resultInfo对象中
            resultInfo.setCode(1);
            resultInfo.setResult(map);
        }



        return resultInfo;
    }

    /**
     * 查询发布云记时的坐标
     * @param userId
     * @return
     */
    public ResultInfo<List<Note>> queryNoteLonAndLat(Integer userId) {
        ResultInfo<List<Note>> resultInfo = new ResultInfo<>();



        //通过id查询云记记录
        List<Note> noteList = noteDao.queryNoteList(userId);

        //判断是否为空
        if (noteList != null && noteList.size() > 0){
            resultInfo.setCode(1);
            resultInfo.setResult(noteList);
        }

        return resultInfo;
    }
}
