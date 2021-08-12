var context = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
var url = window.location.protocol + "//" + window.location.host + context;
var roleAjaxUrl = url + "/ajax/admin/roles/";
var roleForm = $('#roleForm');
var roleId, userId;

function getPrivilegesAjaxUrl(id) {
    return url + "/ajax/admin/roles/" + id + "/privileges/";
}

function getUserRolesAjaxUrl(userId) {
    return url + "/ajax/admin/users/" + userId + "/roles/";
}

function showUserRolesList() {
    $("#userRoleTitle").html(role_msg);
    $(function () {
        table = $('#userRolesList').bootstrapTable({
            url: roleAjaxUrl,
            cache: false,
            columns: [
                {
                    field: 'id'
                },
                {
                    field: 'name'
                },
                {
                    field: 'operate',
                    align: 'center',
                    valign: 'middle',
                    clickToSelect: false,
                    formatter: function (value, row, index) {
                        return '<button data-toggle="modal" title="' + privilege_msg + '" onclick="getPrivilege(' + row.id + ')" class="btn1 rounded" ' +
                            'style="background-color: #3092f3; width: 28px"><i class="fa fa-tasks"></i></button>';
                    }
                },
                {
                    field: 'operate',
                    align: 'center',
                    valign: 'middle',
                    clickToSelect: false,
                    formatter: function (value, row, index) {
                        if (row.id === 1 || row.id === 2) {
                            return '<button  data-toggle="modal" disabled title="' + user_edit_msg + '"  class="btn1 rounded" ' +
                                'style="background-color: #a4a3a6; width: 28px"><i class="fa fa-edit"></i></button>';
                        } else {
                            return '<button data-toggle="modal" title="' + user_edit_msg + '" onclick="updateRoleRow(' + row.id + ');" class="btn1 rounded" ' +
                                'style="background-color: #f3ad05; width: 28px"><i class="fa fa-edit"></i></button>';
                        }
                    }
                },
                {
                    field: 'operate',
                    align: 'center',
                    valign: 'middle',
                    clickToSelect: false,
                    formatter: function (value, row, index) {
                        if (row.id === 1 || row.id === 2) {
                            return '<button disabled title="' + user_delete_msg + '" class="btn1 rounded" ' +
                                'style="background-color: #a4a3a6; width: 28px"><i class="fa fa-trash"></i></button>';
                        } else {
                            return '<button title="' + user_delete_msg + '" onclick="deleteRoleRow(' + row.id + ')" class="btn1 rounded" ' +
                                'style="background-color: #f30018; width: 28px"><i class="fa fa-trash"></i></button>';
                        }


                    }
                }
            ]
        });
    });
    $('#userRoles').modal();
}

function destroyUserRolesList() {
    $('#userRolesList').bootstrapTable('destroy');
    $('.modal-backdrop').hide();
    $.get(userAjaxUrl, updateTableByData);
}

function addRoleRow() {
    $("#addUserRoleTitle").html(user_add_msg);
    $('#inputRole').val("");
    $('#userRoles').modal('hide');
    $('#addUserRole').modal('show');
}

function roleModalShow() {
    $('#addUserRole').modal('hide');
    $('#userRoles').modal('show');
}

function saveRole() {
    closeNoty();
    $.ajax({
        type: "POST",
        url: roleAjaxUrl,
        data: roleForm.serialize()
    }).done(function () {
        $('#userRolesList').bootstrapTable('refresh');
        document.getElementById("roleModalClose").click();
        successNoty(userAdded_msg);
    });
    document.getElementById("idRole").value = "";
}

function deleteRoleRow(id) {
    if (confirm(preDelete_msg)) {
        $.ajax({
            url: roleAjaxUrl + id,
            type: "DELETE"
        }).done(function () {
            $('#userRolesList').bootstrapTable('refresh');
            successNoty(postDelete_msg);
        });
    }
}

function updateRoleRow(id) {
    $("#addUserRoleTitle").html(user_update_msg);
    $.get(roleAjaxUrl + id, function (data) {
        $.each(data, function (key, value) {
            roleForm.find("input[name='" + key + "']").val(value);
        });
        $('#userRoles').modal('hide');
        $('#addUserRole').modal('show');
    });
}

function privilegeModalHide() {
    $('#userPrivilege').modal('hide');
    $('#userRoles').modal('show');
    uncheckElements();
}

function uncheckElements() {
    var uncheck = document.getElementsByTagName('input');
    for (var i = 0; i < uncheck.length; i++) {
        if (uncheck[i].type === 'checkbox') {
            uncheck[i].checked = false;
        }
    }
}

privilegeForm = $('#privilegeForm');

function getPrivilege(id) {
    roleId = id;
    $("#userPrivilegeTitle").html(privileges_list_msg);
    closeNoty();
    $.get(getPrivilegesAjaxUrl(id), function (data) {
        $.each(data, function (key, value) {
            privilegeForm.find("input[type='checkbox'][id='privilege" + value['id'] + "']").prop('checked', true);
        });
        $("#userRoles").modal("hide");
        $('#userPrivilege').modal({backdrop: "static", keyboard: false, show: true});
    });
}

function enableUserPrivilege(privilege) {
    if (privilege.checked) {
        closeNoty();
        $.ajax({
            type: "POST",
            url: getPrivilegesAjaxUrl(roleId) + privilege.value

        }).done(function () {
            successNoty(privilege_add_msg);
        });
    } else {
        closeNoty();
        $.ajax({
            url: getPrivilegesAjaxUrl(roleId) + privilege.value,
            type: "DELETE"
        }).done(function () {
            successNoty(privilege_delete_msg);
        }).fail(function () {
            $(privilege).prop("checked", !privilege.checked);
        });
    }
}

function showRolesForUser(id) {
    userId = id;
    $("#rolesTitle").html(role_msg);
    var userRoles = getUserRolesById(id);
    $(function () {
        table = $('#uRoles').bootstrapTable({
            url: roleAjaxUrl,
            cache: false,
            columns: [
                {
                    field: 'id'
                },
                {
                    field: 'name'
                },
                {
                    field: 'operate',
                    align: 'center',
                    valign: 'middle',
                    clickToSelect: false,
                    formatter: function (value, row, index) {
                        for (var i = 0; i <= userRoles.length; i++) {
                            if (row.id === userRoles[i]) {
                                return "<input type='checkbox' onclick='enableUserRole(this," + row.id + ")' checked>";
                            }
                        }
                        return "<input type='checkbox' onclick='enableUserRole(this," + row.id + ")'>";
                    }
                }
            ]
        });
    });
    $('#roles').modal();
}

function destroyUserRoles() {
    $('#uRoles').bootstrapTable('destroy');
    $.get(userAjaxUrl, updateTableByData);
}

function getUserRolesById(userId) {
    var rolesId = [];
    $.get(getUserRolesAjaxUrl(userId), function (data) {
        $.each(data, function (key, value) {
            rolesId.push(value['id'])
        });
    });
    return rolesId;
}

function enableUserRole(privilege, id) {
    if (privilege.checked) {
        closeNoty();
        $.ajax({
            type: "POST",
            url: getUserRolesAjaxUrl(userId) + id
        }).done(function () {
            successNoty(role_add_msg);
        });
    } else {
        closeNoty();
        $.ajax({
            url: getUserRolesAjaxUrl(userId) + id,
            type: "DELETE"
        }).done(function () {
            successNoty(role_delete_msg);
        }).fail(function () {
            $(privilege).prop("checked", !privilege.checked);
        });
    }
}