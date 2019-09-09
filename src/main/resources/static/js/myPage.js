function changeDiv(n) {
	if( n == 1) {
		var play = document.getElementById("active");
		play.className = "active";
		var hideCom = document.getElementById("community");
		hideCom.className = "hidden";
		var hideLea = document.getElementById("leaveMes");
		hideLea.className = "hidden";
	} else if(n == 2) {
		var play = document.getElementById("active");
		play.className = "hidden";
		var hideCom = document.getElementById("community");
		hideCom.className = "followee";
		var hideLea = document.getElementById("leaveMes");
		hideLea.className = "hidden";
	} else if(n == 3) {
		var play = document.getElementById("active");
		play.className = "hidden";
		var hideCom = document.getElementById("community");
		hideCom.className = "hidden";
		var hideLea = document.getElementById("leaveMes");
		hideLea.className = "follower";
	}
}

function changeLi(n) {
	var lis = document.getElementsByClassName("tagItem");
	for(var i=0;i<lis.length;i++) {
		var li = lis[i];
		if(n-1 == i) {
			li.className = "tagItem show";
		} else {
			li.className = "tagItem";
		}
	}
}

$(document).ready(function() {
	
  	$("#li1").mouseover(function() {
    	$(this).attr("class","activeLi");
  	});
  
  	$("#li2").mouseover(function() {
    	$(this).attr("class","activeLi");
  	});
  
  	$("#li3").mouseover(function() {
    	$(this).attr("class","activeLi");
  	});
  
  	$("#li1").mouseout(function() {
    	$(this).attr("class","");
  	});
  
  	$("#li2").mouseout(function() {
    	$(this).attr("class","");
  	});
  
  	$("#li3").mouseout(function() {
    	$(this).attr("class","");
  	});
  
});

