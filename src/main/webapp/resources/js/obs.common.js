var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function (e, xhr, options) {
    xhr.setRequestHeader(header, token);
});

$(document).ready(function () {
        $('#loading').on('load', function() {
            $('#loading').hide();
        });
});

$(window).submit(function() {
    $('#loading').show();
});