/**
 * 说明：创建App，url为场景地址（可选）
 */
//加载场景代码
var app = new THING.App({ 
    // 场景地址
    "url": "./scene/1"
});

var old_path = null;
window.addEventListener('message',function(e){
	var data = e.data;
	if(old_path == null)
		old_path = data;
    else{
		for(var i=0;i<old_path.length;i++){
			if(app.query('#'+old_path[i])[0] != null){
				app.query('#'+old_path[i])[0].style.color = null;
			}
		}
		old_path = data;
	}
	for(var i=0;i<data.length;i++){
		
		
		if(app.query('#'+data[i])[0] != null){
				if(i==0){
					app.query('#'+data[i])[0].style.color = '#ff3333';
				} 
				else app.query('#'+data[i])[0].style.color = '#7ac749';
			}
		}
},false);