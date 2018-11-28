// Requires loglevel, fast-diff, ot.js

ot.InputWatcher = (function() {
    // Imports
    let Operation = ot.Operation;

    const LAST_VALUE_ATTRIBUTE = 'InputWatcher__last-value';


    class SelectionTracer {
        constructor(content, startPos, endPos) {
            this._contentLength = content.length;
            this._startPos = startPos;
            this._endPos = endPos;
            this._selectionLength = endPos - startPos;

            this._selectedText = content.substr(startPos, this._selectionLength);
            log.debug('Selected text: ' + this._selectedText);
        }

        getSelectionAfter(operation) {
            let traceOperation = this._createTraceOperation(operation.parentId);

            let transformedTraceOperation = operation.transform(traceOperation)[1];
            if (log.getLevel() <= log.levels.DEBUG) {
                log.debug('Transformed operation: ' + JSON.stringify(transformedTraceOperation));
            }

            let newStartPos = this._startPos > 0 ? transformedTraceOperation.components[0].retain
                : 0;

            let newSelectionLength = this._getNewSelectionLength(newStartPos,
                transformedTraceOperation);
            let newEndPos = newStartPos + newSelectionLength;
            return [newStartPos, newEndPos];
        }

        _createTraceOperation(parentId) {
            let traceOperation = Operation.begin()
                .retain(this._startPos)
                .insert('[').delete(this._selectedText).insert(']')
                .retain(this._contentLength - this._endPos);
            traceOperation.parentId = parentId;
            if (log.getLevel() <= log.levels.DEBUG) {
                log.debug('Selection operation: ' + JSON.stringify(traceOperation));
            }
            return traceOperation;
        }

        _getNewSelectionLength(newStartPos, traceOperation) {
            if (this._endPos === this._startPos) {
                return 0;
            } else {
                let traceComponents = newStartPos > 0 ? traceOperation.components.slice(1, 3)
                    : traceOperation.components.slice(0, 2);

                if (traceComponents[0].payload === '[]') {
                    // All selected characters were deleted
                    return 0;
                } else {
                    return traceComponents[1].size();
                }
            }
        }
    }

    class InputWatcher {
        constructor(elementId, onInputChange, mirrorTransformation) {
            this._$element = $('#' + elementId);
            this._element = this._$element[0];
            autosize(this._element);
            const mirrorId = elementId + '_mirror';
            this._$element
                .after(`<div class="form-control" id="${mirrorId}" `
                    + `class="${this._$element.attr(
                        'class')}" contenteditable="plaintext-only"></div>`);
            this._$mirror = $('#' + mirrorId);

            //Insert CSS to hide _$element and show _$mirror instead:
            this._$element.css({
                'position': 'relative',
                'color': 'rgba(0,0,0,0)',
                'z-index': 10,
                'background': 'none',
                'caret-color': 'black'
            });
            this._$mirror.css({
                'position': 'absolute',
                'top': 0,
                'left': 0,
                'right': 0,
                'bottom': 0,
                'height': 'auto',
                'z-index': 5,
            });

            this._mirrorTransformation = mirrorTransformation;

            saveLastInputValue(this._$element);
            this._$element.on("input", function (event) {
                let lastValue = this._$element.data(LAST_VALUE_ATTRIBUTE);
                // noinspection JSPotentiallyInvalidUsageOfClassThis
                let difference = fastDiff.diff(lastValue, this.text());
                onInputChange(event, difference);
                saveLastInputValue(this._$element);
                // noinspection JSPotentiallyInvalidUsageOfClassThis
                this.updateMirrorContent()
            }.bind(this));
        }

        /**
         * Programmatically update the content of the element.
         *
         * Programmatic updates do not fire the input event, so the update needs to
         * manually set the data attribute correctly. If called without arguments,
         * this method just returns the current text.
         *
         * @param newContent The new text
         * @param operation The operation used to make this update
         */
        text(newContent, operation) {
            if (newContent !== undefined) {
                let startPos = this._element.selectionStart;
                let endPos = this._element.selectionEnd;
                log.debug(`Selection: ${startPos} - ${endPos}`);

                let oldContent = this.text();

                if (endPos > 0) {
                    const selectionTracer = new SelectionTracer(oldContent, startPos, endPos);
                    [startPos, endPos] = selectionTracer.getSelectionAfter(operation);
                    log.debug(`New selection: ${startPos} - ${endPos}`);
                }

                this._$element.val(newContent);
                this._element.selectionStart = startPos;
                this._element.selectionEnd = endPos;
                saveLastInputValue(this._$element);
                this.updateMirrorContent();
                autosize.update(this._$element);
            }
            return this._$element.val();
        }

        updateMirrorContent() {
            this._$mirror.html(this._mirrorTransformation(this.text()));
        }
    }

    function saveLastInputValue($element) {
        $element.data(LAST_VALUE_ATTRIBUTE, $element.val());
    }

    return InputWatcher;
})();
