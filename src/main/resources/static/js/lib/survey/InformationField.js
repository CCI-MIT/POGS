class InformationField extends Field {
    constructor(surveyRefence,jsoninfo){
        super(surveyRefence,jsoninfo);
        this.index = this.registerListenerAndGetFieldId(this);
        this.setupHTML();
    }
    setupHTML(){
        let str = "";
        str += '<div class="form-group" id="surveyField_'+this.index+'" style="min-width: 300px;">';
        str += '<label class="control-label text-dark text-left row question-intro">'+this.jsonInfo.question+'</label>';

        if(this.jsonInfo.video_url){
            str += new VideoInformation(this.jsonInfo.video_url).getHTML();
        }
        str += '</div>';
        $('#surveyForm').append(str);
    }
}
class InformationFieldEdit {

    //addIntroduction
    //this.fieldList.push(new InformationFieldEdit(
    constructor(question_number, question, withVideo, video_url) {
        this.questionNumber = question_number;
        var str = "";
        str += '<div class="container question_set" id="question_set' + question_number + '" data-question-type = "introduction">';

        str += '<span><div class="btn btn-sm btn-warning move_toggle">Minimize</div>Information field: <span class="question_number">' + question_number + '</span></span><div class="content">';
        // add text area
        str += '<div class="form-group w_100 row"><label class="col-sm-1 col-form-label">Info:</label>'
               + '<div class="col-sm-9"><textarea class="form-control htmleditor" id="question'+question_number+'" placeholder="Introduction goes here">'+question+'</textarea></div>'
               + '<button type="button" class="btn btn-danger col-sm-1 btn-sm remove-intro remove-question" id="removeQuestion' + question_number + '">remove</button> </div>';
        // str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Question: </label><input class="form-control col-sm-8" type="text" id="question' + question_number + '" placeholder = "Put question here" value="'+question+'">  <button type="button" class="btn btn-danger remove-question btn-sm" id="removeQuestion' + question_number + '">remove</button> </div>';

        if(withVideo){ // add video url
            str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Video url: </label> <input class="form-control col-sm-8 video-input" type="text" id="video_url' + question_number + '" placeholder = "Put video url" value="'+video_url+'">'
                   + '<small id="" class="form-text text-muted">The youtube URL will automatically be changed to the format: https://youtube.com/embed/VIDEOID</small></div>';
        }

        str += '</div>';
        str += '</div>';
        $("#survey").append(str);




        $(".htmleditor").summernote(
            {
                height: 150,
                toolbar: [
                    // [groupName, [list of button]]
                    ['para', ['style','ul', 'ol', 'paragraph']],
                    ['style', ['bold', 'italic', 'underline', 'clear']],
                    ['font', ['strikethrough', 'superscript', 'subscript']],
                    ['fontsize', ['fontsize']],
                    ['color', ['color']],
                    ['insert', ['picture','link','video','table','codeview']]
                ]
                ,
                callbacks: {
                    onImageUpload: function(files) {

                        for(let i=0; i < files.length; i++) {
                            sendFile(files[0],this);
                        }
                    }
                }
            }
        );

        function sendFile(file,editor) {
            //console.log("Send file method");
            let data = new FormData();
            data.append("file", file);
            $.ajax({
                       data: data,
                       type: "POST",
                       url: "/images/upload?"+csrfParamName+"="+csrfToken,
                       cache: false,
                       contentType: false,
                       processData: false,
                       success: function(url) {
                           $(editor).summernote('insertImage', url);
                       }
                   });
        }
    }
    composeFieldFromHTML(){
        let question_set = {};

        question_set["question"] = $("#question"+this.questionNumber).val();
        question_set["type"] = $('#question_set' + this.questionNumber).attr('data-question-type');
        if($("#video_url"+this.questionNumber).val() != undefined){ // if question contains video_url add it
            question_set["video_url"] = $("#video_url"+this.questionNumber).val();
        }

        if($("#placeholder"+this.questionNumber)){ // if question contains placeholder add it
            question_set["placeholder"] = $("#placeholder"+this.questionNumber).val();
        }

        return question_set;

    }
    composeAnswerFromHTML(){
        return "";
    }
}