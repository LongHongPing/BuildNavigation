//打开侧页
function openNav() {
    document.getElementById("3d_map").style.width = "100%";
}

function closeNav() {
    document.getElementById("3d_map").style.width = "0";
}
var loc = null;
var map;
var pb = new BMap.Point(108.838279,34.13223);   // B点坐标
var centerpoint = { // 地图中点-西安电子科技大学南校区 108.840053,34.129522
    lng : 108.840053,
    lat : 34.129522
};
// 创建地图，选定起始点
function createMap(){
    var point = new BMap.Point(centerpoint.lng, centerpoint.lat);    // 构造中点坐标点
    map = new BMap.Map("container");    // 创建Map实例
    map.centerAndZoom(point, 16);  // 初始化地图,设置中心点坐标和地图级别
    map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
}
createMap();
// 1.关键字补全
function G(id) {
    return document.getElementById(id);
}

var ac = new BMap.Autocomplete({    //建立一个自动完成的对象
    "input" : "start",
    "location" : map
});

ac.addEventListener("onhighlight", function(e) {  //鼠标放在下拉列表上的事件
    var str = "";
    var _value = e.fromitem.value;
    var value = "";
    if (e.fromitem.index > -1) {
        value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
    }
    str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;

    value = "";
    if (e.toitem.index > -1) {
        _value = e.toitem.value;
        value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
    }
    str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
    G("searchResultPanel").innerHTML = str;
});

var myValue;
ac.addEventListener("onconfirm", function(e) {    //鼠标点击下拉列表后的事件
    var _value = e.item.value;
    myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
    G("searchResultPanel").innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;

    //setPlace();
});

var pp = new BMap.Point(0, 0);
var pp1= new BMap.Point(0, 0);
function setPlace(){
    map.clearOverlays();    //清除地图上所有覆盖物
    function myFun(){       //填充起始点坐标pp（存在搜索不准的问题，所以设计了手动标点获取坐标功能）
        pp.lng = local.getResults().getPoi(0).point.lng;    //获取第一个智能搜索的结果
        pp.lat = local.getResults().getPoi(0).point.lat;    //获取第一个智能搜索的结果
    }
    var local = new BMap.LocalSearch(map, { //只能搜索
        onSearchComplete: myFun
    });
    local.search(myValue);

//------------------------------------
    //输入 起始点
    function myFun1(){      //填充终点坐标pp1，展示导航路径
        pp1.lng = local1.getResults().getPoi(0).point.lng;    //获取第一个智能搜索的结果
        pp1.lat = local1.getResults().getPoi(0).point.lat;    //获取第一个智能搜索的结果
        var walking = new BMap.WalkingRoute(map, {renderOptions: {map: map, autoViewport: false}});
        walking.search(pp, pp1);
        loc = locationB(pp.lng, pp.lat);// 展示导航路径
        path_3d();
    }
    var local1 = new BMap.LocalSearch(map, { //智能搜索
        onSearchComplete: myFun1
    });
    local1.search(document.getElementById("final").value);

}

//2.清除路线
function mapClear(){
    map.clearOverlays();
}
// 3.清除起始地搜索栏
function startClear(){
    document.getElementById('start').value="";
}
// 3.5鼠标点击生成导航路线事件
function fun(e){
    map.clearOverlays();
    var pp = new BMap.Point(e.point.lng, e.point.lat);
    // TODO ppl要获取final
    var pp1 = pb;
    var walking = new BMap.WalkingRoute(map, {renderOptions: {map: map, autoViewport: true}});
    walking.search(pp, pp1);// 展示导航路径
    loc = locationB(pp.lng, pp.lat);
    path_3d();
    //alert("lalala")
}
// 4.鼠标点击定位起始点
function mouseSearch(){
    document.getElementById('start').disabled=true;
    document.getElementById('mouse').onclick=function(){ unMouseSearch(); };
    map.addEventListener("click",fun);
}
function unMouseSearch(){
    map.clearOverlays();
    document.getElementById('start').disabled=false;
    document.getElementById('mouse').onclick=function(){ mouseSearch(); };
    map.removeEventListener("click",fun);
}
//5.确定方位

function locationB(lng, lat){
    var a = pb.lng - lng;
    var b = pb.lat - lat;
    if(a>=0 && b>=0){
        return 0;
    }
    else if(a>=0 && b<0){
        return 1;
    }
    else if(a<0 && b>=0){
        return 2;
    }
    else if(a<0 && b<0){
        return 3;
    }
}

function path_3d() {
    var room_number = document.getElementById("rom_num").value;
    var relative_location = loc;
    $.ajax({
        type: "GET",
        url: "http://localhost:8888/path",
        data:{
            room_number:room_number,
            relative_location:relative_location
        },
        success: function(result){
            alert(result);
            window.frames[0].postMessage(result,'http://127.0.0.1:9000');
        }
    });
}