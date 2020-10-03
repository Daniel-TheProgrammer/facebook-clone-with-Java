 $(document).ready(function() {

//     console.log(" new comment js jquery ");

     var comment = {
         text: "",
         userId: "",
         postId: ""
     };

     // add post id to comment form
     $('#add-comment').on('show.bs.modal', function(e) {
         var postid = $(e.relatedTarget).data('id');
         $(this).find('#post').val(postid);
     });


     // disable subimt to comment form
     $("#add-comment").submit(function(event) {
         //stop submit the form, we will post it manually.
         event.preventDefault();
         comment_submit();
     });


     // submit comment function
     function comment_submit() {
         comment = {
                 text: $("#text").val(),
                 userId: $("#user").val(),
                 postId: $("#post").val(),
             },

             token = $("#_csrf").val();

         $.ajax({
             type: "POST",
             contentType: "application/json",
             headers: {
                 "X-CSRF-TOKEN": token
             },
             url: "/api/comment/save",
             data: JSON.stringify(comment),
             cache: false,
             timeout: 600000,
             dataType: 'text',
             success: function(result) {
                 // clear & hide modal
                 resetData();
                 $("#add-comment").modal('hide');

                 // appened new comment
                 ajaxGet();
             }

         });
     }


     // reset comment modal
     function resetData() {
         $("#text").val("");
         $("#post").val("");
     }

    // return the new comment on the ui
    function returnDiv(id, text, userId, postId, added ){
     var commentRow =
    '<div class="comment-body-inner" style="">' +
        '<div class="row" >' +
            '<div class="col-sm-2" for="text"> </div>' +
            '<div class="col-sm-8" id="comment-top">' +
                '<div class="row" >' +
                    '<div class="col-sm-8" >' +
                        '<!-- avatar -->' +
                        '<div class="post-avatar">' +
                            '<img src="/img/bird150.jpg" class="img-circle" style="width:100%">' +
                        '</div>' +
                        '<!-- name -->' +
                        '<div class="post-name-date">' +
                            'me  now' +
                        '</div>' +
                    '</div>' +
                    '<!-- edit -->' +
                    '<div class="col-sm-4" >' +
                        '<div class="post-edit" >' +
                        '<a href="/comment/delete/' + id +'">'+ "delete" + '</a>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
                 '<!-- body -->' +
                 '<div class="row" >' +
                    '<div class="rounded-sm" id="comment-body-inner">' +
                               text +
                    '</div>' +
                 '</div>' +
            '</div>' +
        '</div>' +
    '</div>';
    return commentRow;
    }
         // DO GET
         function ajaxGet() {
             $.ajax({
                 type: "GET",
                 //          url : "/api/comment/last/3",
                 url: "/api/comment/last/" + comment.userId + "",
                 dataType: 'json',
                 success: function(result) {
                    commentRow = returnDiv(result.id, result.text, result.userId, result.postId, result.added )
                     $('#newComment').append(commentRow);
                 }

             });

         }

 });

 //    alert("I'm active");
 //     $("selector").on("event", "delegateselector", function(){
 //        // some code...
 //      });

 //    $('div.roomdiv').on('show.bs.modal','#myModal', function (e) {
 //        var getIdFromRow = $(e.relatedTarget).data('id');
 //        $("#buyghc").val(getIdFromRow);
 //    });

 //    $('#add-comment').on('show.bs.modal', function(e) {
 //        var x = $(e.relatedTarget).data('id');
 //        $(this).find('.roomNumber').text(x);
 //    });
 //    $("#get-comment").click(function(){
////      alert("The paragraph was clicked.");
//      console.log( "comment.userId " + comment.userId)
//      ajaxGet();
//    });
