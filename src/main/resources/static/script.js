let token = localStorage.getItem("token");
window.onload = function() {
  if (token) {
    document.getElementById("userSection").style.display = "block";
    loadHistoryChart(); // Only for logged-in users
  }
};
function fetchAQI() {
  const city = document.getElementById("city").value;
  fetch(`/api/air/${city}`)
    .then(res => res.json())
    .then(data => {
      if (data.length > 0) {
        const latest = data[0];
        document.getElementById("aqiDisplay").innerHTML =
          `<h3>City: ${latest.city}</h3>
           <p>AQI: ${latest.value} ${latest.unit}</p>
           <p>Time: ${latest.timestamp}</p>`;
      } else {
        document.getElementById("aqiDisplay").innerText = "No data available";
      }
    });
}
function loadHistoryChart() {
  const city = document.getElementById("city").value || "Delhi";
  fetch(`/api/air/history/${city}`, {
    headers: { Authorization: "Bearer " + token }
  })
    .then(res => res.json())
    .then(data => {
      const labels = data.map(d => d.timestamp.split("T")[0]);
      const values = data.map(d => d.value);
      const ctx = document.getElementById("chartCanvas").getContext("2d");
      new Chart(ctx, {
        type: "line",
        data: {
          labels: labels,
          datasets: [{
            label: "PM2.5",
            data: values,
            borderColor: "blue",
            fill: false
          }]
        }
      });
    })
    .catch(() => {
      document.getElementById("userSection").innerHTML = "<p>Unauthorized. Please login.</p>";
    });
}
function downloadPdf() {
  const city = document.getElementById("city").value;
  window.open(`/api/report/pdf?city=${city}&token=${token}`, '_blank');
}