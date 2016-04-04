function submitConfig(url) {
    $.post(url, $("#settings-form").serialize(), function (data) {
        if (data.code == 1301 || data.code == '1301') {
            $("#warning-block").show();
        }
    });
}
function showSetWindow(show) {
    var window = $("#settings_window");
    if (show) window.show();
    else {
        window.hide();
        $("#settings-form")[0].reset();
    }
}