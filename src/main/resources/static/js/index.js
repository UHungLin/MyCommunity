
$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送异步请求(POST)
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content},
		function(data) {
			data = $.parseJSON(data);
			// 在提示框中显示返回消息
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 2秒后,自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 刷新页面
				if(data.code == 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);
}

// 500页面的JS
$(".full-screen").mousemove(function(event) {
	var eye = $(".eye");
	var x = (eye.offset().left) + (eye.width() / 2);
	var y = (eye.offset().top) + (eye.height() / 2);
	var rad = Math.atan2(event.pageX - x, event.pageY - y);
	var rot = (rad * (180 / Math.PI) * -1) + 180;
	eye.css({
		'-webkit-transform': 'rotate(' + rot + 'deg)',
		'-moz-transform': 'rotate(' + rot + 'deg)',
		'-ms-transform': 'rotate(' + rot + 'deg)',
		'transform': 'rotate(' + rot + 'deg)'
	});
});