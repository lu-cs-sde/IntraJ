<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    
    <!-- If IE use the latest rendering engine -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <!-- Set the page to the width of the device and set the zoon level -->
    <meta name="viewport" content="width = device-width, initial-scale = 1">
    <title>${projectName}</title>
    <link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <#--  Add css file stored locally in  "src/java/magpiebridge/resources/static/app.css"-->
    <link rel="stylesheet" type="text/css" href="static/app.css">
    
    <style>



    </style>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
const navLinks = document.querySelector(".nav-links");
const links = document.querySelectorAll(".nav-links li");

</script>

</head>
<body>
    <nav>
        <div class="logo">
            <img src="img/logo.png" alt="Logo Image">
        </div>
        <ul style="margin-bottom:0px" class="nav-links">
            <li><a href="#">Project Overview</a></li>
            <li><a href="#">Analysis Results</a></li>
            <li><a href="#">Explore CFGs</a></li>
            <li><a href="#">Settings</a></li>
        </ul>
    </nav>

<!-- CONTAINERS -->
<!-- container puts padding around itself while container-fluid fills the whole screen. Bootstap grids require a container. -->
<div class="container" style="word-wrap: break-word;">

    <div class="page-header">
        <h1 style="text-align: center; font-family: 'Orbitron', sans-serif;"> <i>${projectName}</i></h1>
    </div>
</div>



<div class="container" style="word-wrap: break-word;">
  <div class="float-container">

    <div class="float-child">
      <canvas height="350vh" id="pieChart" ></canvas>
    </div>
      
    <div class="float-child" >
      <canvas height="250vh" id="barChart" ></canvas>
    </div>
  </div>
</div>

<div class="container" style="word-wrap: break-word;">

  <#--  libPath  -->
  <details>
    <summary class="danger"> <strong> Library Path </strong> </summary>
    <p class="danger"> ${libPath} </p>
  </details>

  <#--  classPath  -->
  <details>
    <summary class="danger"> <strong> Class Path </strong> </summary>
    <p class="danger"> ${classPath} </p>
  </details>

  <#--  rootPath  -->
  <details>
    <summary class="danger"> <strong> Root Path </strong> </summary>
    <p class="danger"> ${rootPath} </p>
  </details>


  <#--  numCFG  -->
  <details>
    <summary class="danger"> <strong> Number of CFGs under analysis:</strong> ${numCFG} </summary>
  </details> 

  
</div>



<script>


const CHART_COLORS = {
  red: 'rgb(255, 99, 132)',
  orange: 'rgb(255, 159, 64)',
  yellow: 'rgb(255, 205, 86)',
  green: 'rgb(75, 192, 192)',
  blue: 'rgb(54, 162, 235)',
  purple: 'rgb(153, 102, 255)',
  grey: 'rgb(201, 203, 207)',
    brown: 'rgb(165, 42, 42)'
};


const data = {
  labels: ${labels},
  datasets: [
    {
      label: 'Dataset 1',
      data: ${data},
      backgroundColor: Object.values(CHART_COLORS),
    }
  ]
};

  const config = {
  type: 'pie',
  data: data,
  options: {
    events: ['click', 'mousemove', 'touchstart', 'touchmove'],
    onClick: evt => {
      var elements = pieChart.getElementsAtEvent(evt);
      var index = elements[0]._index;
      console.log(index);
      //Alert with values of the clicked slice
      alert("Value: " + data.datasets[0].data[index] + " - Label: " + data.labels[index]);
    },
     maintainAspectRatio: false,
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: '${projectName} -- Feature Analysis'
      }
    }
  },
};





</script>
<script>
 var canvas = document.getElementById("pieChart");
  var pieChart = new Chart(
    canvas,
    config
  );

</script>

<script>
const dataBar = {
  labels: ${barChartLabels},
  datasets: [{
    label: 'Java 1',
    data: ${barChartData1},
    backgroundColor: CHART_COLORS.red,
       borderColor: [
      'rgb(0, 0, 0)',
    ],
    borderWidth: 1
  }, {
    label: 'Java 2',
    data: ${barChartData2},
    backgroundColor: CHART_COLORS.orange,
   borderColor: [
      'rgb(0, 0, 0)',
    ],
    borderWidth: 1
  },{
    label: 'Java 3',
    data: ${barChartData3},
    backgroundColor: CHART_COLORS.yellow,
   borderColor: [
      'rgb(0, 0, 0)',
    ],
    borderWidth: 1
  },{
    label: 'Java 4',
    data: ${barChartData4},
    backgroundColor: CHART_COLORS.green,
    borderColor: [
      'rgb(0, 0, 0)',
    ],idth: 1
  },{
    label: 'Java 5',
    data: ${barChartData5},
    backgroundColor: CHART_COLORS.blue,
     borderColor: [
      'rgb(0, 0, 0)',
    ],
    borderWidth: 1
  },{
    label: 'Java 6',
    data: ${barChartData6},
    backgroundColor: CHART_COLORS.purple,
   borderColor: [
      'rgb(0, 0, 0)',
    ],
    borderWidth: 1
  },{
    label: 'Java 7',
    data: ${barChartData7},
    backgroundColor: CHART_COLORS.grey,
    borderColor: [
      'rgb(0, 0, 0)',
    ],
    borderWidth: 1
  },{
    label: 'Java 8',
    data: ${barChartData8},
    backgroundColor: CHART_COLORS.brown,
    borderColor: [
      'rgb(0, 0, 0)',
    ],
    borderWidth: 1
  }
  ]
};
</script>

<script>
  const barChart = new Chart(
    document.getElementById('barChart'),
    {
      type: 'bar',
      data: dataBar,
      options: {
         indexAxis: 'x',
        legend: {
            display: false,
        },
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: '${projectName} -- Feature Analysis'
          }
        }
      },
    }
  );


</script>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</body>
</html>