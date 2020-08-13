var Changeset = require("ep_etherpad-lite/static/js/Changeset");

// exports.getLineHTMLForExport = function(hook, context){
//
//     var header = _analyzeLine(context.attribLine, context.apool);
//
//     if (header) {
//         return "<" + header + ">" + context.lineContent + "</" + header + ">";
//     }
// }
//
// function _analyzeLine(alineAttrs, apool) {
//     var header = null;
//     if (alineAttrs) {
//         var opIter = Changeset.opIterator(alineAttrs);
//         if (opIter.hasNext()) {
//             var op = opIter.next();
//             header = Changeset.opAttributeValue(op, 'heading', apool);
//         }
//     }
//     return header;
// }


// line, apool,attribLine,text
exports.getLineHTMLForExport = function (hook, context) {
    //console.log("Exporting plugin hook ")
    var author = _analyzeLine(context.attribLine, context.apool);
    //console.log("Found author value: " + (author));
    //console.log("Text: " + (context.text));

    if (author) {
        //console.log("returning HTML ");
        //context.lineContent = "<span class=\"" + author+ "\">" + context.lineContent + "</span>";
    }
}

function _analyzeLine(alineAttrs, apool) {
    var author = null;
    if (alineAttrs) {
        var opIter = Changeset.opIterator(alineAttrs);
        if (opIter.hasNext()) {
            var op = opIter.next();
            author = Changeset.opAttributeValue(op, 'author', apool);
        }
    }
    return author;
}