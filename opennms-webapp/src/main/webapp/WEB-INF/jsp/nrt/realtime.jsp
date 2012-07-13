<html>
  <head>
    <title>NearRealTime</title>
    <link type="text/css" href="../../../css/jquery-ui-1.7.3.custom.css" rel="Stylesheet" />	
    <!-- <script type="text/javascript" src="../../../js/jquery-1.4.2.min.js"></script> -->
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.9/jquery-ui.min.js"></script>
    <script type="text/javascript" src="../../../js/amq_jquery_adapter.js"></script>
    <script type="text/javascript" src="../../../js/amq.js"></script>
    <script type="text/javascript" src="../../../js/d3.v2.js"></script>
    <style type="text/css">
svg {
  border: 1px solid #ccc;
}

path {
  stroke: steelblue;
  stroke-width: 1;
  fill: none;
}

path#id-0 {
  stroke: blue;
}

path#id-1 {
  stroke: green;
}

path#id-2 {
  stroke: red;
}

path#id-3 {
  stroke: yellow;
}

.axis path, .axis line {
  fill: none;
  stroke: #000;
  shape-rendering: crispEdges;
}

line {
  stroke-width: 1;
}
    </style>

    
    <script type="text/javascript">
// Array Remove - By John Resig (MIT Licensed)
Array.prototype.remove = function(from, to) {
	var rest = this.slice((to || from) + 1 || this.length);
	this.length = from < 0 ? this.length + from : from;
	return this.push.apply(this, rest);
};

// Receive inbound JavaScript variables from the controller.
var collectionTaskId = "${collectionTask}";
var rrdGraphString = "${rrdGraphDefn}";

// Create all of the necessary graphing variables.
//var series = null;
var group = null;
var line = null;
var x = null;
var y = null;
var axis = null;
var axisY = null;

// Represents the frequency of data requests.
var refreshInterval = 500;

// Define the graph dimensions.
var margin = {top: 10, right: 10, bottom: 20, left: 40},
    width = 960 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

// Represents the graph duration.
var graphWindowDuration = 60;
var duration =  graphWindowDuration * 1000;

// Create AMQ object.
var amq = org.activemq.Amq;

graphManager = {
	// Contains the actual graph time series data.
	'series': [],

	// Tracks the next line ID to be used for mapping.
	'nextLineId': 0,

	// Maintains a map of metric ID to line ID.
	'metricIdToLineMap': [],

	// Tracks whether we've received a data point yet.
	'isFirstDataSet': true,

	// Retrieves a mapped line ID based on metric ID. Creates a new line if no line ID exists.
	'retrieveLineId': function(metricId) {
		var lineId = this.metricIdToLineMap[metricId];
		if(lineId == null) {
			lineId = this.nextLineId;
			this.metricIdToLineMap[metricId] = lineId;
			this.series[lineId] = new Array();
			this.nextLineId++;
		}
		return lineId;
	},

	// Cycles through all of the series data purging lines not on the visible graph.
	'cleanSeriesData': function(dt) {
		var minGraphTime = new Date(dt-duration);
		var minDataPointTime = d3.min(this.series, function(a) {return d3.min(a,function(b){return b.time;});});
  
		// go through each line.
		for(var ln = 0; ln < this.series.length; ln++) {
			var curLine = this.series[ln];
			for(var dp = 0; dp < curLine.length; dp++) {
				var dataPoint = curLine[dp];
				if(dataPoint.time < minGraphTime.getTime()) {
					curLine.remove(dp);
					//console.log("removing datapoint from series: " + ln);
				}
			}
		}
	},

	// Adds a new JSON data point to the time series data in the correct manner.
	'addJsonDataPoint':function(incomingData) {
		// Push some new data into the time series.
		for(var i = 0; i < incomingData.length; i++) {
			// Retrieve the relevant data from the incoming message.
			var incomingLine = incomingData[i];
			var dataSetLineId = this.retrieveLineId(incomingLine.metricId);
			var dataSetLine = this.series[dataSetLineId];

			// Create the new data point and append it to the array.
			var dataSetPoint = {};
			dataSetPoint.time = incomingLine.timestamp;
			dataSetPoint.value = incomingLine.value;
			dataSetLine.push(dataSetPoint);
		}

		// Create the paths if this is the first set of data retrieved.
		if(this.isFirstDataSet) {
			group.selectAll(".line")
				.data(graphManager.series)
				.enter().append("path")
					.attr("class", "line")
					.attr("id", function(d,i) { return 'id-'+i; })
					.attr("d", line);
			this.isFirstDataSet = false;
		}
 	} 
};

function init() {
	// Create an empty series to force the creation of lines.
	//series = [
	//	[{time: new Date(new Date()), value: 0}],
	//	[{time: new Date(new Date()), value: 0}],
	//	[{time: new Date(new Date()), value: 0}],
	//	[{time: new Date(new Date()), value: 0}]
	//];
	
	var svg = d3.select("#main").append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
			.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

	y = d3.scale.linear()
		.domain([0, 20])
		.range([height, 0]);

	var myEndDate = new Date();
	var myStartDate = new Date(myEndDate - duration);
	var domain0 = [myStartDate, myEndDate];
	
	x = d3.time.scale.utc()
		.domain(domain0)
		.range([0, width]);
	
	line = d3.svg.line()
		.interpolate("linear")
		.x(function(d) { return x(d.time); })
		.y(function(d) { return y(d.value); });

	svg.append("defs").append("clipPath")
		.attr("id", "clip")
		.append("rect")
			.attr("width", width)
			.attr("height", height);
   
	group = svg.append("g").attr("clip-path", "url(#clip)");

//	group.selectAll(".line")
//		.data(series)
//		.enter().append("path")
//			.attr("class", "line")
//			.attr("id", function(d,i) { return 'id-'+i; })
//			.attr("d", line);
	
	axis = svg.append("g")
		.attr("class", "x axis")
		.attr("transform", "translate(0," + height + ")")
		.call(d3.svg.axis().scale(x).orient("bottom"));

	axisY = svg.append("g")
		.attr("class", "y axis")
		.call(d3.svg.axis().scale(y).orient("left"));
}

function tick(incomingData) {
	graphManager.addJsonDataPoint(incomingData);

	// Retrieve the maximum value.
	var curMax = d3.max(graphManager.series, function(a) {return d3.max(a,function(b){return b.value;});});

	// Retrieve the maximum date.
	var dt = new Date(d3.max(graphManager.series, function(a) {return d3.max(a,function(b){return b.time;});}));
  
	// Retrieve the minimum value.
	var curMin = d3.min(graphManager.series, function(a) {return d3.min(a,function(b){return b.value;});});
  
	// update the domain for the graph axis
	x.domain([new Date(dt-duration), dt]);
  
	// update the domain for the graph line
	y.domain([curMin, curMax]);
  
	// slide the x-axis left
	axis.transition()
		.duration(0)
		.ease("linear")
		.call(d3.svg.axis().scale(x));
	  
	// slide the x-axis left
	axisY.transition()
		.duration(0)
		.ease("linear")
		.call(d3.svg.axis().scale(y).orient("left"));

	// redraw the line
	group.selectAll(".line")
		.attr("d", line)
		.attr("transform", null);

	// Clean up any old data from the time series.
	graphManager.cleanSeriesData(dt);
}

// Initialize the ActiveMQ listener.
amq.init({
  uri: '/amq', 
  logging: true, 
  timeout: 45, 
  clientId:(new Date()).getTime().toString() 
});

// Define the AMQ message handler logic and create it.
var lastReceivedMessage = null;
var realTimeHandler = {
	receiveMessage: function(message) {
		if (message != null) {
			var dataSet = eval(message.textContent);
			lastReceivedMessage = message.textContent;
           		tick(dataSet); 
		}
	}
};

amq.addListener('RealtimeHandler', collectionTaskId, realTimeHandler.receiveMessage);

// AJAX Collection Request Timer
var refreshTimerJob = {
	/// Submits the AJAX collection job request and requeues the timer.
	submitJob: function() {
		$.get("/nrt/starter.htm","collectionTask="+collectionTaskId,function(data) { $("#errorDiv").text(data); }, "html");
		var self = this;
		this.timeoutID = setTimeout(function(){refreshTimerJob.submitJob()}, this.refreshInterval);
	},

	/// Used for setting up and starting the refresh timer job.
	setup: function(refreshTicks) {
		this.stop();
		var self = this;
		this.refreshInterval = refreshTicks;
		this.timeoutID = setTimeout(function(){refreshTimerJob.submitJob()}, this.refreshInterval);
	},

	/// Used for stopping the refresh timer.
	stop: function() {
		if(typeof this.timeoutID == "number") {
			clearTimeout(this.timeoutID);
			delete this.timeoutID;
		}
	}
};

$(function() {
	// Set up the input box that allows us to adjust the interval in which data points are graphed.
	$('input[name|="refreshInterval"]').val(refreshInterval);
	$('input[name|="refreshInterval"]').bind('change', function() {
		refreshInterval = $(this).val();
		refreshTimerJob.setup(refreshInterval);
	});
	
	// Set up the input box that allows us to adjust the duration the graph represents.
	$('input[name|="durationInterval"]').val(graphWindowDuration);
	$('input[name|="durationInterval"]').bind('change', function() {
		graphWindowDuration = $(this).val();
		duration =  graphWindowDuration * 1000;
	});

	// Initialize the graph
	init();

	// Start the AJAX job timer.
	refreshTimerJob.setup(refreshInterval);
});
    </script>

</head>
<body>
	<h1>NRT Graph</h1>
	<div id="main"></div>
	<div>Refresh Interval (in ms)<input name="refreshInterval"></input></div>
	<div>Graph Duration (in seconds)<input name="durationInterval"></input></div>
<body>
</html>
