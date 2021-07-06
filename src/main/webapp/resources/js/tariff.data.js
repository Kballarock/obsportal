var tariffAjaxUrl = "ajax/admin/tariff/";

$(function () {
    makeEditable({
            ajaxUrl: tariffAjaxUrl,
            datatableOpts: {
                "columns": [
                    {
                        "data": "name"
                    },
                    {
                        "data": "category"
                    },
                    {
                        "data": "price"
                    },
                    {
                        "data": "description"
                    },
                    {
                        "width" : "20px",
                        "orderable": false,
                        "defaultContent": "",
                        "render": renderEditBtn
                    }
                ],
                "order": [
                    [
                        1,
                        "asc"
                    ]
                ]
            },
            updateTable: function () {
                $.get(tariffAjaxUrl, updateTableByData);
            }
        }
    );
});