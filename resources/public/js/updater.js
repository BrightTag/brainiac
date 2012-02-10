var Updater = (function() {
  var socket;

  return {
    connect: function() {
      socket.send("devs");
    },

    updatePlugin: function(name, data) {
      var template = $("#" + data.type + "-template");
      var content = $.mustache(template.html(), data)
      if ($("div#" + name).length == 0) {
        var plugin = $('<div/>', {'id': name, 'class': Updater.classFor(template)}).html(content);
        $("#plugins").append(plugin);
        Updater.attachWidget(template, plugin);
      } else {
        var plugin = $("div#" + name)
        plugin.html(content);
        Updater.attachWidget(template, plugin);
      }
    },

    classFor: function(template) {
      var classes = "plugin";
      if ($(template.data('class'))) {
        classes = classes + " " + template.data('class');
      }
      return classes;
    },

    attachWidget: function(template, plugin) {
      var widget = template.data('widget');
      if (widget) {
        Widgets[widget](plugin);
      }
    },

    update: function(event) {
      var data = JSON.parse(event.data);
      Updater.updatePlugin(data.name, data);
    },

    subscribe: function() {
      socket = new WebSocket("ws://" + location.host + "/async");
      socket.onmessage = this.update;
      socket.onopen = this.connect;
    }
  };
})();

$(function() {
  Updater.subscribe();
});
