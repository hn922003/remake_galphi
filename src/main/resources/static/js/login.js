
window.onload = function () {
	// 로그인/회원가입 폼 박스 애니메이션
	const signUpButton = document.getElementById('signUp');
	const signInButton = document.getElementById('signIn');
	const container = document.getElementById('container');
	
	signUpButton.addEventListener('click', () => {
	  container.classList.add("right-panel-active");
	});
	
	signInButton.addEventListener('click', () => {
	  container.classList.remove("right-panel-active");
	});
	
	// 한글 입력 방지
	const regExp = /[^0-9a-zA-Z]/g; // 영문+숫자
	const regExp2 = /[ \{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\\(\=]/gi; // 특수문자
	const loginid = document.getElementById('loginid');
	const joinid = document.getElementById('joinid');
	const joinname = document.getElementById('joinname');
	const joinnick = document.getElementById('joinnick');
	
	loginid.addEventListener('input', () => {
		if (regExp.test(loginid.value)) {
			alert('아이디는 영문, 숫자 조합입니다.')
		    loginid.value = loginid.value.replace(regExp, '');
		}
	});
	
	joinid.addEventListener('input', () => {
		if (regExp.test(joinid.value)) {
			alert('아이디는 영문, 숫자 조합입니다.')
		    joinid.value = joinid.value.replace(regExp, '');
		}
	});
	
	joinname.addEventListener('input', () => {
		if (regExp2.test(joinname.value)) {
			alert('이름에 특수문자는 사용할 수 없습니다.')
		    joinname.value = joinname.value.replace(regExp2, '');
		}
	});
	
	joinnick.addEventListener('input', () => {
		if (regExp2.test(joinnick.value)) {
			alert('닉네임에 특수문자는 사용할 수 없습니다.')
		    joinnick.value = joinnick.value.replace(regExp2, '');
		}
	});
	
	
	
	
	
	
	
	
	
}
	
	





