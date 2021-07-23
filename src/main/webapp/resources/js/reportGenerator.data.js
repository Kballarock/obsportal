var reportGeneratorAjaxUrl = "ajax/report/generator/";
var table;
$(function () {
    table = makeEditable({
            ajaxUrl: reportGeneratorAjaxUrl,
            datatableOpts: {
                "columns": [
                    {
                        "data": "name",
                        "font-size": '1em'
                    },
                    {
                        "className": "text-center",
                        "data": "contractType"
                    },
                    {
                        "className": "text-center",
                        "data": "contractNumber"
                    },
                    {
                        "data": "contractDate",
                        "className": "text-center",
                        "render": renderDateBtn
                    },
                    {
                        "data": "unp",
                        "className": "text-center"
                    },
                    {
                        "data": "usersAmount",
                        "className": "text-center"
                    },
                    {
                        "orderable": false,
                        "defaultContent": "",
                        "render": emailBtn
                    },
                    {
                        "orderable": false,
                        "defaultContent": "",
                        "render": renderEditBtn
                    },
                    {
                        "orderable": false,
                        "defaultContent": "",
                        "render": renderDeleteBtn
                    }
                ],
                "order": [
                    [
                        2,
                        "asc"
                    ]
                ]
            },
            updateTable: function () {
                $.get(reportGeneratorAjaxUrl, updateTableByData);
            }
        }
    );
});

function renderDateBtn(date) {
    if (locale === 'ru') {
        var rDate = new Date(date);
        var month = rDate.getMonth() + 1;
        return (rDate.getDate().toString().length > 1 ? rDate.getDate() : "0" + rDate.getDate())
            + "-" + (month.toString().length > 1 ? month : "0" + month) + "-" + rDate.getFullYear();
    }
    return date.substring(0, 10);
}

function emailBtn(data, type, row) {
    if (type === "display") {
        return '<button title="' + email_msg + '" onclick="showEmailList(' + row.id + ')" class="btn1 rounded"' +
            ' style="background-color: #3092f3;  margin-right: -14px"><i class="fa fa-envelope"></i></button>';
    }
}