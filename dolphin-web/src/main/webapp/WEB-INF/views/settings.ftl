<#include "layout/application.ftl"/>

<@application>
<#--<div class="btn-toolbar" role="toolbar">-->
<#--<div class="btn-group">-->
<#--<button type="button" class="btn btn-mini" onclick="showSetWindow(true)">设置</button>-->
<#--<button type="button" class="btn btn-small">按钮 2</button>-->
<#--<button type="button" class="btn btn-toolbar">按钮 3</button>-->
<#--</div>-->
<#--<div class="btn-group">-->
<#--<button type="button" class="btn btn-info">按钮 4</button>-->
<#--<button type="button" class="btn btn-inverse">按钮 5</button>-->
<#--<button type="button" class="btn btn-navbar">按钮 6</button>-->

<#--</div>-->
<#--</div>-->
<div class="panel-heading"><h4 class="panel-title">概要</h4></div>
<div class="panel-body">
    <div class="col-sm-1 text-left">所属应用：dolphin-web</div>
    <div class="col-sm-1 text-left">所属域：dolphin.ubuilding.cn</div>
    <div class="col-sm-1 text-left">创建：吴建峰,2016-03-09 11:35:28</div>
    <div class="col-sm-1 text-left">更新：吴建峰,2016-03-10 11:35:28</div>
</div>
<div class="panel-heading">
    <span class="panel-title"><h4> 配置项</h4></span>
    <span>
    <button type="button" class="btn btn-primary" onclick="showSetWindow(true)">新增</button>
    </span>
</div>
<table class="table table-bordered">
    <thead>
    <tr>
        <th>键</th>
        <th>值</th>
        <th>说明</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
        <#list configs as config>
        <tr>
            <td>${config.name}</td>
            <td>${config.value}</td>
            <td>${config.mark}</td>
            <td>
                <button type="button" class="btn btn-link">编辑</button>
            </td>
        </tr>
        </#list>
    </tbody>
</table>
<div id="settings_window" class="modal hide fade in" style="display:none;">
    <div class="modal-header">
        <a class="close" data-dismiss="modal" onclick="showSetWindow(false)">X</a>

        <h3>设置</h3>
    </div>
    <div class="modal-body">
        <form class="form-signin" accept-charset="UTF-8" method="post" id="settings-form">
            <div class="form-group">
                <label class="col-sm-2 control-label" for="name">键</label>

                <div class="col-sm-4">
                    <input class="form-control" id="name" name="name" type="text"/>
                </div>
                <label class="col-sm-2 control-label" for="value">值</label>

                <div class="col-sm-4">
                    <textarea class="form-control" id="value" name="value" rows="5"></textarea>
                </div>
                <label class="col-sm-2 control-label" for="mark">说明</label>

                <div class="col-sm-4">
                    <textarea class="form-control" id="mark" name="mark" rows="5"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" onclick="submitConfig('${ctx}/api/save/${active}')">保存
                </button>
                <button type="button" class="btn btn-default" onclick="showSetWindow(false)">取消</button>
            </div>
        </form>
    </div>

</div>

<div class="alert alert-block hide" id="warning-block">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    <h4>Warning!</h4>Best check yo self, you're not...
</div>

<script src="../../assets/js/settings.js"></script>
</@application>