<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
  
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>修改密碼</title>
		<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/purecss@3.0.0/build/pure-min.css">
	</head>
	<body style="padding: 20px">
		
		<!-- 修改密碼表單 -->
		<form method="post" action="/JavaWeb/user/change/password" class="pure-form">
			<fieldset>
				<legend>修改密碼頁面</legend>
				Uesrname: <%=session.getAttribute("username") %><p />
				🔑 <input type="password" id="oldpassword" name="oldpassword" placeholder="請輸入舊密碼" required /><p />
				🔑 <input type="password" id="newpassword" name="newpassword" placeholder="請輸入新密碼" required /><p />
				🔑 <input type="password" id="confirmPassword"  placeholder="請再輸入一次新密碼" required /><p />
				<p />
				<button type="submit" class="pure-button pure-button-primary">修改密碼</button>
			</fieldset>
		</form>
			
	</body>
	<script>
		function check() {
			const newpassword = document.getElementById('newpassword').value;
			const confirmPassword = document.getElementById('confirmPassword').value;
			//console.log(password);
			//console.log(confirmPassword);
			if(newpassword ! == confirmPassword){
				alert('二次密碼必須相同!')
				return false;
			}
			return true;
		}
	</script>
</html>