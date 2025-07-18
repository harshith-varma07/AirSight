let token = localStorage.getItem("token");
window.onload = function () {
  if (token) {
    document.getElementById("userSection").style.display = "block";
    loadHistoryChart();
  }
};
function login() {
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;
  fetch("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  })
    .then(res => res.json())
    .then(data => {
      if (data.token) {
        localStorage.setItem("token", data.token);
        window.location.href = "dashboard.html";
      } else {
        document.getElementById("error").innerText = "Login failed";
      }
    })
    .catch(() => {
      document.getElementById("error").innerText = "Server error";
    });
}
// Example: Fetch and display all parameters for a city
function fetchAQI() {
  const city = document.getElementById("city").value;
  fetch(`/api/air/${city}`)
    .then(res => res.json())
    .then(data => {
      if (data.length > 0) {
        let html = `<h3>City: ${city}</h3>`;
        data.forEach(item => {
          html += `<p>${item.parameter.toUpperCase()}: ${item.value} ${item.unit} (at ${item.timestamp})</p>`;
        });
        document.getElementById("aqiDisplay").innerHTML = html;
      } else {
        document.getElementById("aqiDisplay").innerText = "No AQI data available.";
      }
    })
    .catch(() => {
      document.getElementById("aqiDisplay").innerText = "Error fetching data.";
    });
}
function loadHistoryChart() {
  const city = document.getElementById("city").value || "Delhi";
  fetch(`/api/air/history/${city}`, {
    headers: {
      Authorization: "Bearer " + token
    }
  })
    .then(res => res.json())
    .then(data => {
      if (!data || data.length === 0) {
        document.getElementById("userSection").innerHTML += "<p>No chart data available.</p>";
        return;
      }
      const reversed = data.reverse();
      const labels = reversed.map(item => item.timestamp.split("T")[0]);
      const values = reversed.map(item => item.value);
      const ctx = document.getElementById("chartCanvas").getContext("2d");
      new Chart(ctx, {
        type: "line",
        data: {
          labels: labels,
          datasets: [{
            label: "PM2.5 AQI",
            data: values,
            fill: false,
            borderColor: "green",
            tension: 0.3
          }]
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true
            }
          }
        }
      });
    })
    .catch(() => {
      document.getElementById("userSection").innerHTML += "<p>Login required to view chart.</p>";
    });
}
function downloadPdf() {
  const city = document.getElementById("city").value;
  if (!token) {
    alert("Login required to download report.");
    return;
  }
  fetch(`/api/report/pdf?city=${city}`, {
    headers: {
      Authorization: "Bearer " + token
    }
  })
    .then(res => res.blob())
    .then(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `AQI_Report_${city}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
    })
    .catch(() => alert("Failed to download report."));
}