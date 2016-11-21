(function() {
	window.showModal = function() {
		$('.modalWindow').css('display', "block");
		$('.modalWindow').click(function() {
			$('.modalWindow').css('display', "none");
		});
	};
})()