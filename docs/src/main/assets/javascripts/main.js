/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
(function() {
  //init foundation
  $(document).foundation();

  $(document).on("click", ".expand", function(){
      $(this).next('dl').find('dt').addClass('on');
      $(this).removeClass('expand').addClass('collapse');
  });
  $(document).on("click", ".collapse", function(){
      $(this).next('dl').find('dt').removeClass('on');
      $(this).removeClass('collapse').addClass('expand');
  });

  $('.off-canvas-wrap').scroll( function(){
        if ($(this).scrollTop() <= 60) {
            $("#key").addClass("hidden");
        }
        else {
            $("#key").removeClass("hidden");
        }
    });


})();
