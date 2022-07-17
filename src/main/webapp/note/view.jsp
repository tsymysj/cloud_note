<%--
  Created by IntelliJ IDEA.
  User: tsym
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="col-md-9">


    <div class="data_list">
        <div class="data_list_title">
            <span class="glyphicon glyphicon-cloud-upload"></span>&nbsp;
            <c:if test="${empty noteInfo}">
                发布云记
            </c:if>
            <c:if test="${!empty noteInfo}">
                修改云记
            </c:if>

        </div>
        <div class="container-fluid">
            <div class="container-fluid">
                <div class="row" style="padding-top: 20px;">
                    <div class="col-md-12">
<%--                        判断类型列表是否为空，如果为空。提示用户线添加类型--%>
                        <c:if test="${empty typeList}">
                            <h2>暂未查询到云记类型！</h2>
                            <h4><a href="type?actionName=list">添加类型</a></h4>
                        </c:if>
                        <c:if test="${!empty typeList}">
                            <form class="form-horizontal" method="post" action="note">
<%--                                设置隐藏域--%>
                                <input type="hidden" name="actionName" value="addOrUpdate">
                                <input type="hidden" name="noteId" value="${noteInfo.noteId}">

<%--                                设置隐藏域，用来存放用户发布云记时所在地区的经纬度--%>
<%--                                经度--%>
                                <input type="hidden" name="lon" id="lon">
<%--                                 纬度--%>
                                <input type="hidden" name="lat" id="lat">

                            <div class="form-group">
                                <label for="typeId" class="col-sm-2 control-label">类别:</label>
                                <div class="col-sm-8">
                                    <select id="typeId" class="form-control" name="typeId">
                                        <option value="">请选择云记类别...</option>
                                        <c:forEach var="item" items="${typeList}">
                                            <c:choose>
                                                <c:when test="${!empty resultInfo}">
                                                    <option <c:if test="${resultInfo.result.typeId == item.typeId}">selected</c:if> value="${item.typeId}">${item.typeName}</option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option <c:if test="${noteInfo.typeId == item.typeId}">selected</c:if> value="${item.typeId}">${item.typeName}</option>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>



                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <input type="hidden" name="noteId" value="28">
                                <input type="hidden" name="act" value="save">
                                <label for="title" class="col-sm-2 control-label">标题:</label>
                                <div class="col-sm-8">
                                    <c:choose>
                                        <c:when test="${!empty resultInfo}">
                                            <input class="form-control" name="title" id="title" placeholder="云记标题" value="${resultInfo.result.title}">
                                        </c:when>
                                        <c:otherwise>
                                            <input class="form-control" name="title" id="title" placeholder="云记标题" value="${noteInfo.title}">
                                        </c:otherwise>
                                    </c:choose>

                                </div>
                            </div>

                            <div class="form-group">
                                <label for="title" class="col-sm-2 control-label">内容:</label>
                                <div class="col-sm-8">
                                    <c:choose>
                                        <c:when test="${!empty resultInfo}">
                                            <%--准备容器，加载富文本编辑器--%>
                                            <textarea id="content" name="content">${resultInfo.result.content}</textarea>
                                        </c:when>
                                        <c:otherwise>
                                            <%--准备容器，加载富文本编辑器--%>
                                            <textarea id="content" name="content">${noteInfo.content}</textarea>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-sm-offset-4 col-sm-4">
                                    <input type="submit" class="btn btn-primary" onclick="return checkForm();" value="保存">
                                    &nbsp;<span id="msg" style="font-size: 12px;color: red">${resultInfo.msg}</span>
                                </div>
                            </div>
                            </form>
                        </c:if>

                    </div>
                </div>
            </div>
        </div>
    </div>
    <script type="text/javascript">

        var ue;
        $(function (){
            //加载富文本编辑器UE.getEditor('容器id')
            ue = UE.getEditor('content');
        })

        function checkForm(){
            //获取参数
            var typeId = $("#typeId").val();
            var title = $("#title").val();
            var content = ue.getContent();
            //参数判断
            if (isEmpty(typeId)){
                $("#msg").html("请选择云记类型!");
                return false;
            }
            if (isEmpty(title)){
                $("#msg").html("云记标题不能为空!");
                return false;
            }

            if (isEmpty(content)){
                $("#msg").html("云记内容!");
                return false;
            }


            return true;
        }
    </script>

    <script type="text/javascript" src="https://api.map.baidu.com/api?v=2.0&ak=bPBkSmReoELjlDksDxhM9twPOT5lTdFm"></script>
    <script type="text/javascript">
<%--        获取当前位置的经纬度--%>
        var geolocation = new BMap.Geolocation();
        geolocation.getCurrentPosition(function (r){
            //判断是否获取到
            if (this.getStatus() == BMAP_STATUS_SUCCESS){
                // console.log("你的位置："+r.point.lng + ","+r.point.lat);
                //将获取到的坐标设置给隐藏域
                $("#lon").val(r.point.lng)
                $("#lat").val(r.point.lat)
            }else {
                console.log("failed:"+this.getStatus())
            }
        })
    </script>
</div>
