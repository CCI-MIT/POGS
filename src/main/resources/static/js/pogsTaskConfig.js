class PogsTaskConfigEditor {
    init(attrs){
        this.editor.init(attrs.taskConfigId, attrs.configAttributes);
        this.beforeSubmitSetup();
    }
    register(editorImplementation){
        this.editor = editorImplementation;

    }
    beforeSubmitSetup() {
        //console.log('is this working?');
        //console.log('Submit buttons:' + $('form button[type=submit]').length)
        $(document)
            .on('click', 'form button[type=submit]', function(e) {

                let isValid = true; //$(e.target).parents('form').isValid();
                let taskEditorResponse = this.editor.beforeSubmit();

                if($("#configurationName").val()==""){
                    $("#configurationName").addClass("is-invalid");
                    $("#configurationNameError").show();
                    isValid = false;

                }

                if( (typeof taskEditorResponse === 'string') || (taskEditorResponse instanceof String)){
                    isValid = false;
                    alert(taskEditorResponse);
                }

                if(!isValid) {
                    e.preventDefault(); //prevent the default action
                    e.stopPropagation();
                    return false;
                }

            }.bind(this));
    }
}
function isValidUsernameRegex(value){
    var regexp = /^[a-zA-Z0-9]+$/;
    return (regexp.test(value));
}
var pogsTaskConfigEditor = new PogsTaskConfigEditor();

function createOrUpdateAttribute(attributeName, stringValue, intValue, doubleValue,taskConfigurationId, index, id){
    if($("#"+attributeName + '_id').length >0) {
        //$("#"+attributeName + '_id').remove();
        $("#"+attributeName + '_attributeName').remove();
        $("#"+attributeName + 'taskConfigurationId').remove();
        $("#"+attributeName + '_stringValue').remove();
        $("#"+attributeName + '_integerValue').remove();
        $("#"+attributeName + '_doubleValue').remove();
    }
    if($("#"+attributeName + '_id').length == 0) {
        $('<input>').attr({
                              type: 'hidden',
                              id: attributeName + '_id',
                              name: 'attributes[' + index + '].id',
                              value: id
                          }).appendTo('#taskConfigForm');
    }
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
function setupHTMLFieldEditors(){

    $(".htmleditor").summernote(
        {
            height: 200,
            toolbar: [
                // [groupName, [list of button]]
                ['para', ['style','ul', 'ol', 'paragraph']],
                ['style', ['bold', 'italic', 'underline', 'clear']],
                ['font', ['strikethrough', 'superscript', 'subscript']],
                ['fontsize', ['fontsize']],
                ['color', ['color']],
                ['insert', ['picture','link','video','table','codeview']]
            ],
            callbacks: {
                onImageUpload: function(files) {
                    console.log("Send file called success override: " + this);
                    for(let i=0; i < files.length; i++) {
                        sendFile(files[0],this);
                    }
                }
            }
        });

    function sendFile(file,editor) {
        console.log("Send file method");
        data = new FormData();
        data.append("file", file);
        $.ajax({
                   data: data,
                   type: "POST",
                   url: "/images/upload?[(${_csrf.parameterName})]=[(${_csrf.token})]",
                   cache: false,
                   contentType: false,
                   processData: false,
                   success: function(url) {
                       $(editor).summernote('insertImage', url);
                   }
               });
    };
}