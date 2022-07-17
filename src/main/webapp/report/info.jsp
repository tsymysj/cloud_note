<%--
  Created by IntelliJ IDEA.
  User: tsym
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<div class="col-md-9">

    <div class="data_list">
        <div class="data_list_title"><span class="glyphicon glyphicon-signal"></span>&nbsp;数据报表 </div>
        <div class="container-fluid">
            <div class="row" style="padding-top: 20px;">
                <div class="col-md-12">
<%--                    柱状图的容器--%>
                    <div id="monthChart" style="height: 500px;"></div>
<%--                    百度地图的加载--%>
                    <h3 align="center">用户地区分布图</h3>
                    <div id="baiduMap" style="height: 600px;width: 100%">

                    </div>
                </div>
            </div>
        </div>
    </div>

</div>
<%--
    Echarts报表的使用
        1.下载Echarts的依赖（js文件）
        2.在需要使用的页面引入Echarts的JS文件
        3.为echarts准备一个具备高宽的dom容器
        4.通过echarts.init方法初始化一个echarts实例并通过setOption方法生成一个报表
--%>
<script type="text/javascript" src="https://api.map.baidu.com/api?v=1.0&&type=webgl&ak=bPBkSmReoELjlDksDxhM9twPOT5lTdFm"></script>
<script type="text/javascript" src="static/echarts/echarts.min.js"></script>
<script type="text/javascript">

    /**
     * 通过约根查询对应的云记数量
     * */
    $.ajax({
        type:'get',
        url:'report',
        data:{
            actionName:"month"
        },
        success:function (result) {
            console.log(result)
            if (result.code == 1){
                //得到月份（x轴的数据）
                var monthArray = result.result.monthArray;
                //得到月份对应得云记数量（y轴数据）
                var dataArray = result.result.dataArray;
                loadMonthCharts(monthArray,dataArray)
            }
        }
    })


    /**
     * 加载柱状图
     */
    function loadMonthCharts(monthArray,dataArray) {
        var myCharts =  echarts.init(document.getElementById('monthChart'));
        // x轴显示的名称
        let dataAxis = monthArray;
        // prettier-ignore
        let data = dataArray;
        let yMax = 30;
        let dataShadow = [];
        for (let i = 0; i < data.length; i++) {
            dataShadow.push(yMax);
        }
        var option = {
            //标题配置
            title: {
                text: '按月统计',
                subtext: '通过月份查询对应的云记数量',
                left:'center'
            },
            //提示框
            tooltip:{},
            //x轴
            xAxis: {
                data: dataAxis,
                axisTick:{
                    show:false
                },
                axisLine:{
                    show:false
                }
            },
            //y轴
            yAxis: {
                max:yMax,
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                axisLabel: {
                    color: '#999'
                }
            },
            dataZoom: [
                {
                    type: 'inside'
                }
            ],
            //系列
            series: [
                {
                    type: 'bar',//柱状图
                    showBackground: true,
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#83bff6' },
                            { offset: 0.5, color: '#188df0' },
                            { offset: 1, color: '#188df0' }
                        ])
                    },
                    emphasis: {
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: '#2378f7' },
                                { offset: 0.7, color: '#2378f7' },
                                { offset: 1, color: '#83bff6' }
                            ])
                        }
                    },
                    data: data//y轴数据
                }
            ]
        };
        // Enable data zoom when user click bar.


        myCharts.setOption(option)
    }

    /**
     * 通过用户的坐标查询云记
     * */
    $.ajax({
        type:"get",
        url:"report",
        data:{
            actionName: "location"
        },
        success:function (result){
            console.log(result);
            if (result.code == 1){
                //加载百度地图
                loadBaiduMap(result.result)
            }
        }
    })
    /**
     * 加载百度地图
     */
    function loadBaiduMap(markers) {
        //加载地图实例
        var map = new BMapGL.Map("baiduMap")
        //设置地图中心点
        var point = new BMapGL.Point(116.404,39.915);
        //地图初始化
        map.centerAndZoom(point,10);
        // 添加比例尺控件
        var scaleCtrl = new BMapGL.ScaleControl();
        map.addControl(scaleCtrl);
        // 添加缩放控件
        var zoomCtrl = new BMapGL.ZoomControl();
        map.addControl(zoomCtrl);

        //判断是否有点坐标
        if (markers != null && markers.length > 0){
            //将用户所在的位置设置到中心点
            map.centerAndZoom(new BMapGL.Point(markers[0].lon,markers[0].lat),10);
            //循环在地图上添加点标记
            for (var i = 1; i < markers.length; i++){
                var marker = new BMapGL.Marker(new BMapGL.Point(markers[i].lon, markers[i].lat));
                map.addOverlay(marker);
            }
        }


    }

</script>
