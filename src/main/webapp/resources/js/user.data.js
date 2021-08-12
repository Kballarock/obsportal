var userAjaxUrl = "ajax/admin/users/";

$(function () {
    makeEditable({
            ajaxUrl: userAjaxUrl,
            datatableOpts: {
                "columns": [
                    {
                        "data": "name"
                    },
                    {
                        "data": "email"
                    },
                    {
                        "data": "registered",
                        "className": "text-center",
                        "render": renderDateBtn
                    },
                    {
                        "orderable": false,
                        "data": "enabled",
                        "className": "text-center",
                        "render": function (data, type, row) {
                            if (type === "display") {
                                return "<input type='checkbox' " + (data ? "checked" : "") + " onclick='enable($(this)," + row.id + ");'/>";
                            }
                            return data;
                        }
                    },
                    {
                        "data": "provider",
                        "className": "text-center"
                    },
                    {
                        "data": "roles",
                        "render": "[, ].name"
                    },
                    {
                        "width": "30px",
                        "orderable": false,
                        "defaultContent": "",
                        "render": renderRolesBtn
                    },
                    {
                        "width": "30px",
                        "orderable": false,
                        "defaultContent": "",
                        "render": renderEditBtn
                    },
                    {
                        "width": "30px",
                        "orderable": false,
                        "defaultContent": "",
                        "render": renderDeleteBtn
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ],
                "createdRow": function (row, data, dataIndex) {
                    if (!data.enabled) {
                        $(row).attr("data-userEnabled", false);
                    }
                }
            },
            updateTable: function () {
                $.get(userAjaxUrl, updateTableByData);
            }
        }
    );
});

function enable(checkbox, id) {
    var enabled = checkbox.is(":checked");
    $.ajax({
        url: userAjaxUrl + id,
        type: "POST",
        data: "enabled=" + enabled
    }).done(function () {
        checkbox.closest("tr").attr("data-userEnabled", enabled);
        successNoty(enabled ? user_enabled_msg : user_disabled_msg);
    }).fail(function () {
        $(checkbox).prop("checked", !enabled);
    });
}

function renderDateBtn(date) {
    if (locale === 'ru') {
        var rDate = new Date(date);
        var month = rDate.getMonth() + 1;
        return (rDate.getDate().toString().length > 1 ? rDate.getDate() : "0" + rDate.getDate())
            + "-" + (month.toString().length > 1 ? month : "0" + month) + "-" + rDate.getFullYear();
    }
    return date.substring(0, 10);
}

function renderRolesBtn(data, type, row) {
    if (type === "display") {
        return '<button title="' + role_setting_msg + '" onclick="showRolesForUser(' + row.id + ')" class="btn1 rounded"' +
            ' style="background-color: #3092f3; width: 28px"><i class="fa fa-gear"></i></button>';
    }
}