import $ from 'jquery';
import '@fancyapps/fancybox'
import '@fancyapps/fancybox/dist/jquery.fancybox.css'
import '@fortawesome/fontawesome-free/css/all.css'
import ClipboardJS from 'clipboard';
import '../css/main.css'


$(document).ready(function () {

    $('.lightbox, .topics-resources a[href*=youtu], #quote a[href*=youtu], .half a[href*=youtu]').click(function () {
        var href = $(this).attr('href');
        $(this).attr('href', href + '?autoplay=1&autohide=1&showinfo=0&controls=1');
        $.fancybox({
            'padding': 0,
            'href': this.href.replace(new RegExp('watch\\?v=', 'i'), 'embed/'),
            'type': 'iframe',
            'width': 1000,
            'autoPlay': true,
            'height': 560,
            'autoSize': false,
            fitToView: true
        });

        return false;
    });

    $('li#search').click(function () {
        $('#search-nav').slideToggle();
        $('li#search').toggleClass('close');
        $('#searchheaderform input').focus();
    });

    // Anchor
    $('body.guide main h2[id], body.guide main h3[id], body.guide main h4[id]').each(function() {
        var id = $(this).attr('id');
        var anchor = $('<a />').attr('href', '#' + id)
            .addClass('anchor')
            .text('§');
        $(this).append(anchor);
    });


    $('pre.prettyprint').each(function () {
        var _this = $(this);
        var button = $('<button />')
            .text('Copy')
            .addClass('copy-button button button-small animate')
            .attr('data-clipboard-text', _this.text());
        _this.append(button)
    });
    var clipboard = new ClipboardJS('.copy-button');
    clipboard.on('success', function(e) {
        e.trigger.textContent = 'Copied';
        window.setTimeout(function() {
            e.trigger.textContent = 'Copy';
        }, 2000);
        e.clearSelection();
    });
    clipboard.on('error', function(e) {
        alert('An error occurred.')
    });

});

// Terminal animation compiled with Babel
'use strict';

window.onload = function () {
    if (document.querySelector('body').id == 'index') {

        $('.wordWrapper').addClass('ready');
        $('#wordWrapper div').each(function () {
            $(this).css('opacity', '1');

            var characters = $(this).text().split('');

            $(this).empty();
            var jThis = jQuery(this);
            $.each(characters, function (i, el) {
                $(jThis).append('<span>' + el + '</span');
            });
        });

        var wordCounter = 0;

        function switchWords() {
            swapClasses();
            setTimeout(switchWords, 3000);

            function swapClasses() {
                $('#wordWrapper div').each(function (index) {
                    $('span', this).removeClass();
                    $('span', this).addClass('up-' + wordCounter);
                });
                if (wordCounter === 4) {
                    wordCounter = 0;
                }
                else {
                    wordCounter++;
                }
            }
        }

        switchWords();

        var type = function type() {
            if (i < txt.length) {
                document.querySelector('.terminal .typed').innerHTML += txt.charAt(i);
                i++;
                setTimeout(type, speed);

                if (i == 22) {
                    document.querySelector('.terminal .typed').innerHTML = document.querySelector('.terminal .typed').innerHTML.replace('"Hello World!"', '<span class=\'terminal-lime\'>"Hello World!"</span>');
                }
            }
        };

        var handler = function handler(entries, observer) {
            var _iteratorNormalCompletion = true;
            var _didIteratorError = false;
            var _iteratorError = undefined;

            try {
                for (var _iterator = entries[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
                    var entry = _step.value;

                    if (entry.isIntersecting) {
                        document.querySelector('.terminal .typed-placeholder').style.display = 'none';
                        type();
                    }
                }
            } catch (err) {
                _didIteratorError = true;
                _iteratorError = err;
            } finally {
                try {
                    if (!_iteratorNormalCompletion && _iterator.return != null) {
                        _iterator.return();
                    }
                } finally {
                    if (_didIteratorError) {
                        throw _iteratorError;
                    }
                }
            }
        };

        var i = 0;
        var txt = 'return "Hello World!";';
        var speed = 100;
        var observer = new IntersectionObserver(handler);
        observer.observe(document.querySelector('.terminal .terminal-blue'));
    }

};

// Filter toggle
$('#projects #proj-filters li').click(function () {

    if ($(this).hasClass('active')) {
        $(this).toggleClass('active');
    }
    else {
        $('#projects #proj-filters li').removeClass('active');
        $(this).toggleClass('active');
    }
});

//Nav animation
function removeNavClasses() {
    $('#scope').removeClass('why-scope learn-scope projects-scope community-scope');
    $('.drop-menu').removeClass('active');
    $('.has-menu').removeClass('active');
}

$('#why-target').mouseenter(function () {
    removeNavClasses();
    $('#scope').addClass('why-scope');
    $('#why-items').addClass('active');
});
$('#learn-target').mouseenter(function () {
    removeNavClasses();
    $('#scope').addClass('learn-scope');
    $('#learn-items').addClass('active');
});
$('#project-target').mouseenter(function () {
    removeNavClasses();
    $('#scope').addClass('projects-scope');
    $('#project-items').addClass('active');
});
$('#community-target').mouseenter(function () {
    removeNavClasses();
    $('#scope').addClass('community-scope');
    $('#community-items').addClass('active');
});
$('.drop-menu').mouseenter(function () {
    $('this').addClass('active');
});
$('.drop-menu ul').mouseleave(function () {
    removeNavClasses();
});
$('.drop-menu').mouseleave(function () {
    removeNavClasses();
});

//Accessibility
$('#why-hov span').focus(function () {
    removeNavClasses();
    $('#scope').addClass('why-scope');
    $('#why-items').addClass('active');
});
$('#learn-hov span').focus(function () {
    removeNavClasses();
    $('#scope').addClass('learn-scope');
    $('#learn-items').addClass('active');
});
$('#projects-hov span').focus(function () {
    removeNavClasses();
    $('#scope').addClass('projects-scope');
    $('#project-items').addClass('active');
});
$('#community-hov span').focus(function () {
    removeNavClasses();
    $('#scope').addClass('community-scope');
    $('#community-items').addClass('active');
});
$('#logo-focus').focus(function () {
    setTimeout(function () {
        $('#logo-focus').addClass('focused');
    }, 150);
});
$('#logo-focus').blur(function () {
    $(this).removeClass('focused');
});
$('#logo-focus').click(function () {
    $(this).removeClass('focused');
});
$('.drop-menu').focus(function () {
    $('this').addClass('active');
});
$('#logo-focus, #nav-items a, #search').focus(function () {
    removeNavClasses();
});

// Mobile nav
document.querySelector('#hamburger').onclick = function () {
    $('.mobile-close').fadeIn();
    $('#mobile-nav').toggleClass('isOpen').css('opacity', 1);
    $('#mobile-nav-open #hamburger').removeClass('grow').addClass('shrink');
    $('#mobile-nav-open .mobile-class').addClass('grow');
    $('body').addClass('noscroll');
    $('#mobile-nav > .relative').delay(350).fadeIn();
}
document.querySelector('.mobile-close').onclick = function () {
    $('.mobile-close').fadeOut();
    $('#mobile-nav').toggleClass('isOpen');
    $('#mobile-nav-open .mobile-class').removeClass('grow').addClass('shrink');
    $('#mobile-nav-open #hamburger').addClass('grow');
    $('body').removeClass('noscroll');
    resetSlide();
    $('#mobile-nav > .relative').fadeOut(80);
}
$('#mobile-why').click(function () {
    if ($('#mobile-why-items').is(':hidden')) {
        resetSlide();
    }
    $('> .mobile-nav-arrow', this).toggleClass('flipped');
    $('#mobile-why-items').slideToggle();
});
$('#mobile-learn').click(function () {
    if ($('#mobile-learn-items').is(':hidden')) {
        resetSlide();
    }
    $('> .mobile-nav-arrow', this).toggleClass('flipped');
    $('#mobile-learn-items').slideToggle();
});
$('#mobile-projects').click(function () {
    if ($('#mobile-project-items').is(':hidden')) {
        resetSlide();
    }
    $('> .mobile-nav-arrow', this).toggleClass('flipped');
    $('#mobile-project-items').slideToggle();
});
$('#mobile-community').click(function () {
    if ($('#mobile-community-items').is(':hidden')) {
        resetSlide();
    }
    $('> .mobile-nav-arrow', this).toggleClass('flipped');
    $('#mobile-community-items').slideToggle();
});

function resetSlide() {
    $('.mobile-expanded-category').slideUp();
    $('.mobile-nav-arrow').removeClass('flipped');
}

$('header #search').keypress(function (e) {
    if (e.which == 13 || e.which == 32) {
        $('#search-nav').slideToggle();
        $(this).toggleClass('close');
        $('#searchheaderform input').focus();
    }
});
