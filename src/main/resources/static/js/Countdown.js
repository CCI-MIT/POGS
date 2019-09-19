//TODO: put it in an external file
class Countdown{
    constructor(countDownDate, htmlReference, finalFunction){
        // Update the count down every 1 second
        this.countDownDate = countDownDate;
        this.htmlReference = htmlReference;
        this.finalFunction = finalFunction;

        this.finalMessage = "Redirecting ...";
        function trailingZeros(val) {
            if (val < 10) {
                return '0' + val;
            } else {
                return val;
            }
        }

        this.x = setInterval(function () {

            // Get todays date and time
            var now = new Date().getTime();

            // Find the distance between now an the count down date
            var distance = this.countDownDate - now;

            // Time calculations for days, hours, minutes and seconds
            var days = Math.floor(distance / (1000 * 60 * 60 * 24));
            var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            var seconds = Math.floor((distance % (1000 * 60)) / 1000);

            if (isNaN(days)) {
                days = 0;
            }
            if (isNaN(hours)) {
                hours = 0;
            }
            if (isNaN(minutes)) {
                minutes = 0;
            }
            if (isNaN(seconds)) {
                seconds = 0;
            }

            //
            //trailingZeros
            if (distance > 0) {
                if (document.getElementById(this.htmlReference) != null) {
                    document.getElementById(this.htmlReference).innerHTML =
                        ((minutes > 0) ? (trailingZeros(minutes.toString()) + ':' ) : (''))
                        + trailingZeros(seconds.toString())
                        + ((minutes > 0) ? (' minutes')
                        : (' seconds'));
                }
            } else {
                if (document.getElementById(this.htmlReference) != null) {
                    document.getElementById(this.htmlReference).innerHTML = this.finalMessage;
                }
            }

            // If the count down is finished, write some text
            if (distance < 0) {
                clearInterval(this.x);
                this.finalFunction.call(pogs);
            }
        }.bind(this), 1000);
    }
    updateCountDownDate(countDownDate) {
        this.countDownDate = countDownDate;
    }
    updateFinalMessage(msg){
        this.finalMessage = msg;
    }
    cancelCountDown(){
        clearTimeout(this.x);
    }
}