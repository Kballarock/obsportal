var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function (e, xhr, options) {
    xhr.setRequestHeader(header, token);
});

$(document).ready(function () {
    $('#loading').on('load', function () {
        $('#loading').hide();
    });
});

$(window).submit(function () {
    $('#loading').show();
});

var context, form;

function makeEditable(ctx) {
    context = ctx;
    context.datatableApi = $("#datatable").DataTable(
        $.extend(true, ctx.datatableOpts,
            {
                "ajax": {
                    "url": context.ajaxUrl,
                    "dataSrc": ""
                },
                pagingType: 'numbers',
                language: {
                    "sUrl": getLangUrl()
                },
                processing: true
            }
        ));

    function getLangUrl() {
        if (locale === "ru") {
            return 'resources/js/localisation/Russian.json';
        } else {
            return 'resources/js/localisation/English.json';
        }
    }

    form = $('#detailsForm');
    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    });

    $.ajaxSetup({cache: false});
}

function add() {
    $("#modalTitle").html(user_add_msg);
    form.find(":input").val("");
    $("#editRow").modal();
}

function updateRow(id) {
    $("#modalTitle").html(user_update_msg);
    $.get(context.ajaxUrl + id, function (data) {
        $.each(data, function (key, value) {
            form.find("input[name='" + key + "']").val(value);
        });
        $('#editRow').modal();
    });
}

function deleteRow(id) {
    if (confirm(preDelete_msg)) {
        $.ajax({
            url: context.ajaxUrl + id,
            type: "DELETE"
        }).done(function () {
            context.updateTable();
            successNoty(postDelete_msg);
        });
    }
}

function updateTableByData(data) {
    context.datatableApi.clear().rows.add(data).draw();
}

function save() {
    closeNoty();
    $.ajax({
        type: "POST",
        url: context.ajaxUrl,
        data: form.serialize()
    }).done(function () {
        $("#editRow").modal("hide");
        context.updateTable();
        successNoty(userAdded_msg);
    });
}

var failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(key) {
    closeNoty();
    new Noty({
        text: "<span class='fa fa-lg fa-check'></span> &nbsp;" + key,
        type: 'success',
        layout: "bottomRight",
        timeout: 2000
    }).show();
}

function failNoty(jqXHR) {
    closeNoty();
    var errorInfo = JSON.parse(jqXHR.responseText);
    failedNote = new Noty({
        text: "<span class='fa fa-lg fa-exclamation-circle'></span> &nbsp;" + errorInfo.typeMessage + "<br>" + '-&nbsp;' + errorInfo.details.join("<br>" + '-&nbsp;'),
        type: "error",
        layout: "bottomRight",
        timeout: 10000
    }).show();
}

function renderEditBtn(data, type, row) {
    if (type === "display") {
        return '<button title="' + user_edit_msg + '" onclick="updateRow(' + row.id + ')" class="btn1 rounded" ' +
            'style="background-color: #f3ad05;margin-right: -4px;width: 28px"><i class="fa fa-edit"></i></button>';
    }
}

function renderDeleteBtn(data, type, row) {
    if (type === "display") {
        return '<button title="' + user_delete_msg + '" onclick="deleteRow(' + row.id + ')" class="btn1 rounded" ' +
            'style="background-color: #f30018; margin-left: -14px;width: 28px"><i class="fa fa-trash"></i></button>';
    }
}