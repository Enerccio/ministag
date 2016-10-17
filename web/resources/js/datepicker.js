(function() {
	$(document).ready(function() {
		$(".datepicker").datepicker({
		    buttonText: "Zvolte datum",
		    dateFormat: "dd.mm.yy",
		    currentText: $(".datepicker").val() 
		});
	});
})();