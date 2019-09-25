class Text {
    constructor(text, backgroundColor, fontColor){
        this.text = text;
        this.backgroundColor = backgroundColor;
        this.fontColor = fontColor;
        this.words = [];
    }
    process(){
        //This should create the words lists.
        var wordsA = this.text.split(' ');
        for(var i =0 ; i < wordsA.length; i++){
            this.words.push(new Word(wordsA[i],this.backgroundColor, this.fontColor));
        }

    }
}
class Word {
    constructor(word,backgroundColor, fontColor){
        this.word = word;
        this.backgroundColor = backgroundColor;
        this.fontColor = fontColor;
    }
}
class Line {
    constructor(tempLine,wordList){
        this.words = wordList;
        this.line = tempLine;
        this.shouldBreakLine = false;
    }

    allWordsHaveSameColor(){
        if(this.words.length ==0 ) return true;
        if(this.words.length ==1 ) return true;
        if(this.words[0]!=null) {
            var color = this.words[0].backgroundColor;
            for (var a in this.words) {

                if (this.words[a].backgroundColor != color) {
                    return false;
                }
            }
        }
        return true;
    }
    lastColorOfLine(){
        if(this.words.length ==1 ) return null;
        var color = this.words[0].backgroundColor;
        for(var a in this.words){
            color = this.words[a].backgroundColor;
        }
        return color;
    }
}

var DEBUG_CANVAS_DRAW = true;
function _log(message){
    if(DEBUG_CANVAS_DRAW){
        console.log(message);
    }
}
class CanvasTextToImage {
    constructor(textList, container, maxWidth){

        this.textList = textList;// [{textContent:"", backgroundColor:"", fontColor: ""}]
        _log("CanvasTextToImage[constructor]: Number of texts: " + this.textList.length);
        this.words = [];
        this.containerRef = document.getElementById(container);
        this.maxWidth = maxWidth;

        //DEFAULT CONFIG FOR CANVAS
        this.width = maxWidth;
        this.height = 8000;//BIG SIZE TO BE UPDATED AFTER FONT MEASUREMENTS

        this.fontFamily = "Times";
        this.fontSize = "18px";
        this.textAlign = "left";
        this.completeLinesBackgroundsUntilEnd = true;

        this.initAndDraw();

    }
    setupHTML(){
        if(this.containerRef == null) {
            console.log("ERROR: container not found! ");
            return;
        }
        //Create temp canvas
        this.tempCanvas = this.createCanvas(true);
        this.setupCanvasContext(this.tempCanvas);

        _log("CanvasTextToImage[setupHTML]: Conainer exists: " + (this.containerRef != null));
    }
    setupCanvasContext(canvas){

        this.ctx = canvas.getContext('2d')
        this.ctx.save();
        this.ctx.clearRect(0, 0, this.width, this.height);
        this.ctx.font = " " + this.fontSize + " " + this.fontFamily;//bold
        this.ctx.textAlign = this.textAlign;
        //this.ctx.fillStyle = this.fontColor;
    }
    createCanvas(isTemporary, width, height){
        var tempCanvas = document.createElement('canvas');
        var canvasTempId = "cvs" + new Date().getTime();
        tempCanvas.setAttribute("id", canvasTempId);
        if(isTemporary){
            tempCanvas.setAttribute("style","display:none");
        } else {
            //set width and height
            tempCanvas.height = height;
            tempCanvas.width = width;
        }
        this.containerRef.appendChild(tempCanvas);
        return document.getElementById(canvasTempId);
    }
    destroyTempCanvas(){
        this.tempCanvas.parentNode.removeChild(this.tempCanvas);
    }
    initAndDraw(){
        //0 setup html
        this.setupHTML();
        //1 Transform each text into Texts
        this.texts = [];
        for(var i = 0 ; i < this.textList.length; i ++){
            this.texts.push(new Text(this.textList[i].textContent,this.textList[i].backgroundColor, this.textList[i].fontColor));
            //2 process the words and lines
            this.texts[i].process();
            //3 Merge texts
            for(var j =0; j < this.texts[i].words.length; j ++){
                this.words.push(this.texts[i].words[j]);
            }
        }
        //4 attempt to merge it all.
        this.lines = this.fragmentText(this.maxWidth);
        this.destroyTempCanvas();
        var canvasHeight = (this.lines.length * parseInt(this.fontSize, 0)) +3; //3 pixels of extra space
        //Create canvas
        this.tempCanvas = this.createCanvas(false,this.maxWidth, canvasHeight);
        this.setupCanvasContext(this.tempCanvas);
        this.draw();
    }
    draw(){
        var lineHeight = parseInt(this.fontSize, 0);
        var BACKGROUND_Y0_OFFSET = 5;
        this.lines.forEach(function (line, i) {

            if(line.shouldBreakLine){
                this.ctx.fillStyle = line.words[0].backgroundColor;
                this.ctx.fillRect(((this.textAlign == "center") ? (this.width / 2) : (0)), (i) * lineHeight + BACKGROUND_Y0_OFFSET, this.width,/*widthText,*/ lineHeight);//+5
            }
            if(this.completeLinesBackgroundsUntilEnd){
                if(line.allWordsHaveSameColor()){
                    this.ctx.fillStyle = line.words[0].backgroundColor;
                    this.ctx.fillRect(((this.textAlign == "center") ? (this.width / 2) : (0)), (i) * lineHeight + BACKGROUND_Y0_OFFSET, this.width,/*widthText,*/ lineHeight);
                } else {
                    this.ctx.fillStyle = line.lastColorOfLine();
                    this.ctx.fillRect(((this.textAlign == "center") ? (this.width / 2) : (0)), (i) * lineHeight + BACKGROUND_Y0_OFFSET, this.width,/*widthText,*/ lineHeight);
                }
            }
            var lastXPosition = 0;
            for(var j=0; j< line.words.length; j++){
                var wordWidth = this.ctx.measureText(line.words[j].word + " ").width;

                /// draw background rect assuming height of font
                /// color for background
                if(!line.shouldBreakLine){
                    this.ctx.fillStyle = line.words[j].backgroundColor;
                    this.ctx.fillRect(lastXPosition  , (i) * lineHeight + BACKGROUND_Y0_OFFSET, wordWidth + 1, lineHeight);//+1
                }

                /// color for background
                this.ctx.fillStyle = line.words[j].fontColor;
                this.ctx.fillText(line.words[j].word + " ", (lastXPosition),
                                  (i + 1) * lineHeight);

                lastXPosition = lastXPosition + wordWidth;
            }
        }.bind(this));
        this.ctx.restore();
    }
    fragmentText() {
        var words = this.words,
            maxWidth = this.maxWidth,
            lines = [],
            line = new Line("",[]);
        while (words.length > 0) {
            while (this.ctx.measureText(words[0].word).width >= maxWidth) {
                var tmp = words[0].word;
                words[0].word = tmp.slice(0, -1);
                if (words.length > 1) {
                    words[1].word = tmp.slice(-1) + words[1].word;
                } else {
                    words.push(new Word(tmp.slice(-1),words[0].backgroundColor, words[0].fontColor));
                }
            }

            if (words[0].word.indexOf("\n") != -1) {
                //words[0] = words[0].replace("\n\n","\\n \\n");

                var possibleNewLines = words[0].word.split(/\r\n|\r|\n/g);
                //console.log("WORDS:")
                //console.log(">"+words[0].word + "<");
                //console.log("Has ("+possibleNewLines.length+")");
                for(let j=0; j< possibleNewLines.length; j ++){
                    //console.log(j + ": >" + possibleNewLines[j] + "<")
                }
                var oldWord = words.shift();
                for (var i = possibleNewLines.length -1; i >= 0; i--) {
                    console.log( i + ": >"+possibleNewLines[i]+"<")
                    if (possibleNewLines[i] != "") {
                        words.unshift( new Word(possibleNewLines[i],oldWord.backgroundColor, oldWord.fontColor));
                        if(i!=0) {
                            words.unshift(new Word("&&%%MUSTBREAKNEWLINE", oldWord.backgroundColor,
                                                   oldWord.fontColor));
                        }
                    } else {
                        if(i!=0) {
                            words.unshift(new Word("&&%%MUSTBREAKNEWLINE", oldWord.backgroundColor,
                                                   oldWord.fontColor));
                        }
                    }
                }

            }

            let wordForWidth = words[0].word;
            if (wordForWidth == ("&&%%MUSTBREAKNEWLINE")) {
                wordForWidth = "";
            }
            if (this.ctx.measureText(line.line + wordForWidth).width < maxWidth) {
                if (words[0].word != ("&&%%MUSTBREAKNEWLINE")) {
                    var wordToAdd = words.shift();
                    line.line += wordToAdd.word + " ";
                    line.words.push(wordToAdd);
                } else {
                    console.log(" - >" + line.line + "<");
                    console.log("WORD WAS A NEW LINE BREAK");
                    var breakLineWord = words.shift();
                    breakLineWord.word = "";
                    line.shouldBreakLine = true;
                    line.words.push(breakLineWord);
                    lines.push(line);
                    line = new Line("",[]);
                }

            } else {
                lines.push(line);
                line = new Line("",[]);

            }
            if (words.length === 0) {
                lines.push(line);
            }
        }
        return lines;
    }
}