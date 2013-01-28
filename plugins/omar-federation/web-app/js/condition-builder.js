//Helps in creating a structure will help later.
function makeStruct(names) {
    var names = names.split(' ');
    var count = names.length;
    function constructor() {
        for (var i = 0; i < count; i++) {
            this[names[i]] = arguments[i];
        }
    }
    return constructor;
}

//Recursive method to parse the condition and generate the query. Takes the selector for the root condition

//Recursive method to iterate over the condition tree and generate the query


