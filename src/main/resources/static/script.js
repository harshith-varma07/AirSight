function fetchData() {
    let loc = document.getElementById('locationInput').value;
    fetch(`/api/air/${loc}`)
        .then(res => res.json())
        .then(data => {
            let result = document.getElementById('result');
            result.innerHTML = JSON.stringify(data, null, 2);
        });
}