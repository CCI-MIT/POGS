class PogsTaskConfigEditor {
    init(attrs){
        this.editor.init(attrs.taskConfigId, attrs.configAttributes);
        this.beforeSubmitSetup();
    }
    register(editorImplementation){
        this.editor = editorImplementation;

    }
    beforeSubmitSetup() {
        $(document)
            .on('click', 'form button[type=submit]', function(e) {
                var isValid = true; //$(e.target).parents('form').isValid();
                var taskEditorResponse = this.editor.beforeSubmit();
                if($("#configurationName").val()==""){
                    alert("Configuration name can\'t be empty!");
                    return false;
                }

                if( typeof taskEditorResponse === 'string' || taskEditorResponse instanceof String){
                    isValid = false;
                    alert(taskEditorResponse);
                }


                if(!isValid) {
                    e.preventDefault(); //prevent the default action
                }
            }.bind(this));
    }
}
var pogsTaskConfigEditor = new PogsTaskConfigEditor();

function createOrUpdateAttribute(attributeName, stringValue, intValue, doubleValue,taskConfigurationId, index, id){
    if($("#"+attributeName + '_id').length >0) {
        $("#"+attributeName + '_id').remove();
        $("#"+attributeName + '_attributeName').remove();
        $("#"+attributeName + 'taskConfigurationId').remove();
        $("#"+attributeName + '_stringValue').remove();
        $("#"+attributeName + '_integerValue').remove();
        $("#"+attributeName + '_doubleValue').remove();
    }
    $('<input>').attr({
                          type: 'hidden',
                          id: attributeName + '_id',
                          name: 'attributes['+index+'].id',
                          value: id
                      }).appendTo('#taskConfigForm');
    $('<input>').attr({
                          type: 'hidden',
                          id: attributeName + '_attributeName',
                          name: 'attributes['+index+'].attributeName',
                          value: attributeName
                      }).appendTo('#taskConfigForm');

    $('<input>').attr({
                          type: 'hidden',
                          id: attributeName + 'taskConfigurationId',
                          name: 'attributes['+index+'].taskConfigurationId',
                          value: taskConfigurationId
                      }).appendTo('#taskConfigForm');

    if(stringValue !=null) {
        $('<input>').attr({
                              type: 'hidden',
                              id: attributeName + '_stringValue',
                              name: 'attributes[' + index + '].stringValue',
                              value: stringValue
                          }).appendTo('#taskConfigForm');
    }
    if(intValue != null) {
        $('<input>').attr({
                              type: 'hidden',
                              id: attributeName + '_integerValue',
                              name: 'attributes[' + index + '].integerValue',
                              value: intValue
                          }).appendTo('#taskConfigForm');
    }
    if(doubleValue != null) {
        $('<input>').attr({
                              type: 'hidden',
                              id: attributeName + '_doubleValue',
                              name: 'attributes[' + index + '].doubleValue',
                              value: doubleValue
                          }).appendTo('#taskConfigForm');
    }
}