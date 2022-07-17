package com.xxxx.note.web;

import cn.hutool.core.util.StrUtil;
import com.xxxx.note.po.Note;
import com.xxxx.note.po.NoteType;
import com.xxxx.note.po.User;
import com.xxxx.note.service.NoteService;
import com.xxxx.note.service.NoteTypeService;
import com.xxxx.note.util.Page;
import com.xxxx.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/note")
public class NoteServlet extends HttpServlet {

    private NoteService noteService = new NoteService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //设置首页导航的高亮值
        req.setAttribute("menu_page","note");

        //得到用户行为
        String actionName = req.getParameter("actionName");

        if ("view".equals(actionName)){
            //进入发表页面
            noteView(req,resp);
        }else if ("addOrUpdate".equals(actionName)){
            //添加或修改云记
            addOrUpdate(req,resp);
        }else if ("detail".equals(actionName)){
            //查寻云记详情
            noteDetail(req,resp);
        }else if ("delete".equals(actionName)){
            //查寻云记详情
            noteDelete(req,resp);
        }
    }

    /**
     * 删除云记
     * @param req
     * @param resp
     */
    private void noteDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String noteId = req.getParameter("noteId");
        Integer code = noteService.deleteNote(noteId);
        resp.getWriter().write(code+"");
        resp.getWriter().close();
    }

    /**
     * 查寻云记详情
     * @param req
     * @param resp
     */
    private void noteDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String noteId = req.getParameter("noteId");
        Note note = noteService.findNoteById(noteId);
        req.setAttribute("note",note);
        req.setAttribute("changePage","note/detail.jsp");
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }

    /**
     * 添加或修改操作
     * @param req
     * @param resp
     */
    private void addOrUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //接收参数
        String typeId = req.getParameter("typeId");
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        //获取经纬度
        String lon = req.getParameter("lon");
        String lat = req.getParameter("lat");

        //如果修改操作，需要接收noteId
        String noteId = req.getParameter("noteId");

        //调用service
        ResultInfo<Note> resultInfo = noteService.addOrUpdate(typeId,title,content,noteId,lon,lat);
        if (resultInfo.getCode() == 1){
            resp.sendRedirect("index");
        }else {
            req.setAttribute("resultInfo",resultInfo);

            String url = "note?actionName=view";
            //如果时修改操作，需要传递noteId
            if(StrUtil.isBlank(noteId)){
                url += "&noteId="+noteId;
            }
            req.getRequestDispatcher(url).forward(req,resp);
        }
    }

    /**
     * 进入发布云记页面
     * @param req
     * @param resp
     */
    private void noteView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /*修改操作*/
        //得到要修改的云记ID
        String noteId = req.getParameter("noteId");
        //通过noteId查询云集对象
        Note note = noteService.findNoteById(noteId);
        //将note对象设置到请求域中
        req.setAttribute("noteInfo",note);

        //获取对象
        User user = (User) req.getSession().getAttribute("user");
        //获取类型列表
        List<NoteType> typeList = new NoteTypeService().findTypeList(user.getUserId());
        //将类型列表存入作用域中
        req.setAttribute("typeList",typeList);

        req.setAttribute("changePage","note/view.jsp");
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }


}
