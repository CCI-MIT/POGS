//<canvas id="cvs"></canvas>

function drawTextAsImage(text, backgroundColor, foregroundColor, container) {
    var canvas = document.getElementById('cvs'),
        ctx = canvas.getContext('2d'),
        input = document.getElementById('input'),
        width = +(canvas.width = 350),
        height = +(canvas.height = 8000),
        fontFamily = "Times",
        fontSize = "18px";
        //fontColour = "#000000";

    var textAlign = "left";

    function fragmentText(text, maxWidth) {
        var words = text.split(' '),
            lines = [],
            line = "";
        if (ctx.measureText(text).width < maxWidth) {
            return [text];
        }
        while (words.length > 0) {
            while (ctx.measureText(words[0]).width >= maxWidth) {
                var tmp = words[0];
                words[0] = tmp.slice(0, -1);
                if (words.length > 1) {
                    words[1] = tmp.slice(-1) + words[1];
                } else {
                    words.push(tmp.slice(-1));
                }
            }

            if (words[0].indexOf("\n") != -1) {
                //words[0] = words[0].replace("\n\n","\\n \\n");

                var possibleNewLines = words[0].split(/\r\n|\r|\n/g);

                //console.log("Has ("+possibleNewLines.length+")new line -" + words[0] + "-")
                words.shift();
                for (var i = possibleNewLines.length; i > 0; i--) {
                    //console.log(">"+possibleNewLines[i-1]+"<")
                    if (possibleNewLines[i - 1] != "") {
                        words.unshift(possibleNewLines[i - 1]);
                        if (i != 1) {
                            words.unshift("&&%%MUSTBREAKNEWLINE");
                        }
                    }
                }
            }

            if (ctx.measureText(line + words[0]).width < maxWidth) {
                if (words[0] != ("&&%%MUSTBREAKNEWLINE")) {
                    line += words.shift() + " ";
                } else {
                    words.shift();
                    lines.push(line);
                    line = "";
                }

            } else {

                lines.push(line);
                line = "";

            }
            if (words.length === 0) {
                lines.push(line);
            }
        }
        return lines;
    }

    function draw(text, backgroundColor, fontColor) {
        ctx.save();
        ctx.clearRect(0, 0, width, height);
        ctx.font = " " + fontSize + " " + fontFamily;//bold
        ctx.textAlign = textAlign;
        ctx.fillStyle = fontColour;
        var lines = fragmentText(input.value, width - parseInt(fontSize, 0));
        //canvas.height = lines.length * parseInt(fontSize,0);
        console.log(lines.length * parseInt(fontSize, 0));
        lines.forEach(function (line, i) {
            ctx.fillText(line, ((textAlign == "center") ? (width / 2) : (0)),
                         (i + 1) * parseInt(fontSize, 0));
        });
        ctx.restore();

    }
}