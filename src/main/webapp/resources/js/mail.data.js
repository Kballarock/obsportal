var url = window.location.href.replace('/reportGenerator', '');
var table, contractId;
var emailForm = $('#emailForm');

function getEmailUrl(id) {
    return url + '/ajax/report/generator/' + id + '/emailList/';
}

function showEmailList(id) {
    contractId = id;
    $("#orgEmailListTitle").html(user_update_msg);
    $(function () {
        table = $('#datatable2').bootstrapTable({
            url: getEmailUrl(id),
            cache: false,
            columns: [
                {
                    field: 'id'
                },
                {
                    field: 'email'
                },
                {
                    field: 'operate',
                    align: 'center',
                    valign: 'middle',
                    clickToSelect: false,
                    formatter: function (value, row, index) {
                        return '<button data-toggle="modal" title="' + user_edit_msg + '" onclick="updateEmailRow(' + row.id + ');" class="btn1 rounded" ' +
                            'style="background-color: #f3ad05; width: 28px"><i class="fa fa-edit"></i></button>';

                    }
                },
                {
                    field: 'operate',
                    align: 'center',
                    valign: 'middle',
                    clickToSelect: false,
                    formatter: function (value, row, index) {
                        return '<button title="' + user_delete_msg + '" onclick="deleteEmailRow(' + row.id + ')" class="btn1 rounded" ' +
                            'style="background-color: #f30018; width: 28px"><i class="fa fa-trash"></i></button>';
                    }
                }
            ]
        });
    });
    $('#orgEmailList').modal();
}

function destroyDataTable() {
    $('#datatable2').bootstrapTable('destroy');
    $('.modal-backdrop').hide();
}

function addEmailRow() {
    $("#emailModalTitle1").html(user_add_msg);
    $('#inputOrgEmail').val("");
    $('#orgEmailList').modal('hide');
    $('#emailModal').modal('show');
}

function orgEmailModalShow() {
    $('#emailModal').modal('hide');
    $('#orgEmailList').modal('show');
}

function updateEmailRow(id) {
    $("#emailModalTitle1").html(user_update_msg);
    $.get(getEmailUrl(contractId) + id, function (data) {
        $.each(data, function (key, value) {
            emailForm.find("input[name='" + key + "']").val(value);
        });
        $('#orgEmailList').modal('hide');
        $('#emailModal').modal('show');
    });
}

function saveEmail() {
    closeNoty();
    $.ajax({
        type: "POST",
        url: getEmailUrl(contractId),
        data: emailForm.serialize()
    }).done(function () {
        $('#datatable2').bootstrapTable('refresh');
        document.getElementById("emailModalClose").click();
        successNoty(userAdded_msg);
    });
    document.getElementById("idEmail").value = "";
}

function deleteEmailRow(id) {
    if (confirm(preDelete_msg)) {
        $.ajax({
            url: getEmailUrl(contractId) + id,
            type: "DELETE"
        }).done(function () {
            $('#datatable2').bootstrapTable('refresh');
            successNoty(postDelete_msg);
        });
    }
}