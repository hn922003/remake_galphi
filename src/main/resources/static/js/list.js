//컨텐츠 출력 개수 지정 함수
function setViewcount() {
	// 컨텐츠 총 개수
	let last = $('#last').val()
	last = parseInt(last) // 컨텐츠 총 개수
	console.log('last: ' + last)
	
	// 더보여줄 콘텐츠 박스의 시작 인덱스
	let morecount = $('#morecount').val()
	morecount = parseInt(morecount)
	console.log('morecount: ' + morecount)
	
	// morecount 박스인덱스 만큼 6번 반복해서 콘텐츠 더 보여주기
	for (i = morecount; i < morecount + 6; i++) {
		$('.trc').eq(i).css('display', 'block');
		//console.log(i)
		// 마지막 댓글 출력시 더보기 버튼 감추기 
		if (i >= last-10) {
			$('#morediv').css('display', 'none');
		}
	}
	
	// 다음 더보기 버튼을 위해서 변수 넘겨주기
	morecount = parseInt(morecount) + 6 // 다음 보여줄 컨텐츠의 시작인덱스 +6
	$('#morecount').val(morecount)
}

function test() {
	console.log('JS 파일 연결 완료')
}

// top버튼 애니메이션
$(function() {
    $(window).scroll(function() {
        if ($(this).scrollTop() > 200) {
            $('#toTop').fadeIn();            
        } else {
            $('#toTop').fadeOut();
        }
    });
    
    $("#toTop").click(function() {
        $('html, body').animate({
            scrollTop : 0
        }, 100);
        return false;
    });
});


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










