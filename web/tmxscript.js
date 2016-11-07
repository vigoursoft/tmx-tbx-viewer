/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global tbl */

var orig;
var tbl;

// user clicks on cell in table
// Requires: add onclick to td 
function tdclick(x) {

    var cellIndex = x.cellIndex;
    var rowIndex = x.parentNode.rowIndex;

    // we need this value later
    var element = x.innerHTML;

    orig = element;

    if (document.getElementById("edittext") === null) {
        x.innerHTML = "<textarea id='edittext' id='edittext' style='width: 100%; height: 100%; border: none;'>"
                + element + "</textarea>";
        orig = document.getElementById("edittext").value;//innerHTML;
    }

    // alert(document.getElementById("edittext").innerHTML);
    // document.getElementById("edittext").onblur = function() {blurtext(this)};
    document.getElementById("edittext").onmouseout = function () {
        blurtext(this, orig, cellIndex, rowIndex);
    };
}

function blurtext(x, y, cell, row) {

    var who = document.getElementById('content');
    var actualRow = who.rows[row].cells[0].innerHTML;
    x.parentNode.innerHTML = x.value;

    if (x.value === orig) {
        alert('No change');
        return;
    }

    var ref = window.location.href;
    var res = ref.split("?");
    ref = res[0] + "?save=" + x.value + "&row=" + actualRow + "&cell=" + cell;
    //alert (ref);

    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                console.log(xhr.responseText);
            } else if (xhr.status == 400) {
                console.log('There was an error 400');
            } else {
                console.log('something else other than 200 was returned');
            }
        }
    };
    xhr.open("GET", ref, true);

    xhr.send();

}








