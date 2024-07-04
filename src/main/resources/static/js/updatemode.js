// 댓글 수정 모드 변환 함수
function updatemode(ISBN, idx, score, memo) {
	//console.log('ISBN: ' + ISBN)
	//console.log('idx: ' + idx)
	//console.log('score: ' + score)
	//console.log('memo: ' + memo)
	
	// 수정할 댓글 정보를 받아와 입력칸에 꽂고 입력칸으로 이동
	let emptyscore = document.getElementById('score'); // 댓글 입력칸 별점
	let emptymemo = document.getElementById('memo'); // 댓글 입력칸 메모
	let emptyh2 = document.getElementById('h2'); // 댓글 입력 제목
	let cobtn = document.getElementById('cobtn'); // 댓글 입력 버튼
	let frm = document.getElementById('frm'); // 댓글 입력 폼
	
	emptyscore.value = parseInt(score)
	emptymemo.value = memo
	emptyh2.innerText = '책갈피 변경'
	cobtn.innerHTML = '<i class="bi bi-pencil"></i>&nbsp;후기수정'
	frm.setAttribute('action', '/comment/edit');
	
	
}




