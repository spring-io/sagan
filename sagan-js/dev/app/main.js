var createGuide = require('page/guide/main');

var initSearch = require('feature/search/main');
var initSearchFacets = require('feature/searchFacets/main');
var initFilterableList = require('feature/filterableList/main');
var initMobileSupport = require('feature/mobileSupport/main');
var initInfoPopups = require('feature/infoPopups/main');
var initPlatformDownloads = require('feature/platformDownloads/main');

var most = require('most');
var $ = require('jquery');

var slice = Array.prototype.slice;
var dataAttrRx = /^data-/i;

var features = {
    search: initSearch,
    'search-facets': initSearchFacets,
    'filterable-list': initFilterableList,
    // TODO: Break createGuide into multiple features
    guide: createGuide,
    'mobile-support': initMobileSupport,
    'info-popups': initInfoPopups,
    'platform-downloads': initPlatformDownloads
};

initFeatures(features, document).each(function(features) {
    $(window).unload(function() {
        destroyFeatures(features);
    });
});

/**
 * Scans the document for the set of desired features, initializes
 * each, and returns a Stream containing a single array, whose contents
 * are the initialized features.
 * @param features
 * @param document
 * @returns {Object|*}
 */
function initFeatures(features, document) {
    return scanFeatures(features, document)
        .map(function(key) {
            return features[key]();
        })
        .reduce(function(initialized, feature) {
            initialized.push(feature);
            return initialized;
        }, []);
}

/**
 *
 * @param {object} features hash of feature initializers by name
 * @param {Document} document Document whose <html> is annotated with the
 *  set of desired features
 * @returns {object} most.js Stream containing the string names of
 *  the desired features
 */
function scanFeatures(features, document) {
    return most.fromArray(slice.call(document.documentElement.attributes))
        .map(function(attr) {
            var name = attr.name;
            return dataAttrRx.test(name) && name.slice(5);
        })
        .filter(function(name) {
            return name && name in features;
        });
}

function destroyFeatures(features) {
    features.forEach(function(feature) {
        feature.destroy();
    });
}