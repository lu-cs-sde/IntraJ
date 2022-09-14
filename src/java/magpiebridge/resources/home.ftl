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
    
    <style>



body{
    overflow: visible;

}

nav{
    height: 5rem;
    width: 100vw;
    display: flex;
    z-index: 10;
    background-color: #053742;
    box-shadow: 0 3px 20px rgba(0,0,0,0.2);
}

/* Styling Logo*/

.logo{
    text-align: left;
    padding-left: 20px;
}

.logo img{
    height: auto;
    width: 5em;
    left-padding: 20px;
}

/* Styling Navigation Links*/

.nav-links{
    width: 80vw;
    display: flex;
    padding: 0 0.7vw;
    justify-content: space-evenly;
    align-items: center;
    text-transform: uppercase;
    list-style: none;
    font-weight: 600;
}

.nav-links li a{
    margin: 0 0.7vw;
    text-decoration: none;
    color: #ffffff;
    transition: all ease-in-out 350ms;
    padding: 10px;
}

.nav-links li a:hover{
    color:#000;
    background-color: #fff;
    padding: 10px;
    border-radius: 50px;
}

.nav-links li{
    position:relative;
}

.nav-links li a:hover::before{
    width: 80%;
}




/*Responsive Adding Media Queries*/

@media screen and (max-width: 800px){
    nav{
        position: fixed;
        z-index: 3;
    }
    .hamburger{
        display:block;
        position: absolute;
        cursor: pointer;
        right: 5%;
        top: 50%;
        transform: translate(-5%, -50%);
        z-index: 2;
        transition: all 0.7s ease;
    }
    .nav-links{
        background: #053742;
        position: fixed;
        opacity: 1;
        height: 100vh;
        width: 100%;
        flex-direction: column;
        clip-path: circle(50px at 90% -20%);
        -webkit-clip-path: circle(50px at 90% -10%);
        transition: all 1s ease-out;
        pointer-events: none;
    }
    .nav-links.open{
        clip-path: circle(1000px at 90% -10%);
        -webkit-clip-path: circle(1000px at 90% -10%);
        pointer-events: all;
    }
    .nav-links li{
        opacity: 0;
    }
    .nav-links li:nth-child(1){
        transition: all 0.5s ease 0.2s;
    }
    .nav-links li:nth-child(2){
        transition: all 0.5s ease 0.4s;
    }
    .nav-links li:nth-child(3){
        transition: all 0.5s ease 0.6s;
    }
    .nav-links li:nth-child(4){
        transition: all 0.5s ease 0.7s;
    }
    .nav-links li:nth-child(5){
        transition: all 0.5s ease 0.8s;
    }
    .nav-links li:nth-child(6){
        transition: all 0.5s ease 0.9s;
        margin: 0;
    }
    .nav-links li:nth-child(7){
        transition: all 0.5s ease 1s;
        margin: 0;
    }

    li.fade{
        opacity: 1;
    }

    /* Navigation Bar Icon on Click*/

        .toggle .bars1{
            transform: rotate(-45deg) translate(-5px, 6px);
        }

        .toggle .bars2{
            transition: all 0s ease;
            width: 0;
        }

        .toggle .bars3{
            transform: rotate(45deg) translate(-5px, -6px);
        }

}

     
      .danger {
        background-color: #DDDDDD;
        border-left: 3px solid #555555;
        padding-left: 10px;
      }
      .jumbotron {
          background-color: #2E2D88;
          color: white;
          padding: 1px 1px 1px 1px;
          text-align: center;
      }
      /* Adds borders for tabs */
      .tab-content {
          border-left: 1px solid #ddd;
          border-right: 1px solid #ddd;
          border-bottom: 1px solid #ddd;
          padding: 10px;
      }
      .nav-tabs {
          margin-bottom: 0;
      }

      .float-container {
     
        padding: 20px;
      }

    .float-child {
      width: 50%;
      float: left;
      padding: 20px;
   
    }  
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



  <#--  <details>
    <summary class="danger"> <strong> Project Name </strong> </summary>
    <p class="danger"> ${projectName} </p>
  </details>
  
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
  </details>  -->

  
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