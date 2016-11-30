(function() {
	window.showModal = function(data, component) {
		if (data.status && data.status === 'success') {
			component = '#' + (component.replace(new RegExp(':', 'g'), '\\\:'));
			$(component).find('.modalWindow').css('display', "block");
			$(component).find('.modalWindow').click(function() {
				$(component).find('.modalWindow').css('display', "none");
			});
		}
	};
})()