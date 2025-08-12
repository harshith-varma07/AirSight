// Global variables
let currentUser = null;
let isLoggedIn = false;
const API_BASE_URL = 'http://localhost:8080/api';

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    createParticles();
    
    // Check for existing user session
    const savedUser = localStorage.getItem('airSightUser');
    if (savedUser) {
        try {
            currentUser = JSON.parse(savedUser);
            isLoggedIn = true;
            updateNavbarForLoggedInUser();
        } catch (e) {
            localStorage.removeItem('airSightUser');
        }
    }
    
    loadDashboardData();
    setInterval(loadDashboardData, 300000); // Update every 5 minutes
});

// Create floating particles animation
function createParticles() {
    const particlesContainer = document.getElementById('particles');
    const particleCount = 50;

    for (let i = 0; i < particleCount; i++) {
        const particle = document.createElement('div');
        particle.className = 'particle';
        particle.style.left = Math.random() * 100 + '%';
        particle.style.animationDelay = Math.random() * 20 + 's';
        particle.style.animationDuration = (Math.random() * 10 + 10) + 's';
        
        // Random colors
        const colors = ['var(--neon-blue)', 'var(--neon-purple)', 'var(--neon-green)'];
        particle.style.background = colors[Math.floor(Math.random() * colors.length)];
        
        particlesContainer.appendChild(particle);
    }
}

// Load dashboard data
async function loadDashboardData() {
    showLoading(true);
    
    try {
        // Load available cities first
        const citiesResponse = await fetch(`${API_BASE_URL}/aqi/cities`);
        const citiesData = await citiesResponse.json();
        
        if (citiesData.success && citiesData.cities && citiesData.cities.length > 0) {
            // Get AQI data for multiple cities
            const topCities = citiesData.cities.slice(0, 6); // Get top 6 cities
            const multiResponse = await fetch(`${API_BASE_URL}/aqi/multiple?${topCities.map(city => `cities=${encodeURIComponent(city)}`).join('&')}`);
            const multiData = await multiResponse.json();
            
            if (multiData.success && multiData.data) {
                const cities = Object.values(multiData.data).map(cityData => ({
                    name: cityData.city,
                    country: '',
                    aqi: cityData.aqiValue,
                    category: cityData.category,
                    coordinates: [0, 0] // We don't have coordinates from backend
                }));
                
                // Update main AQI with first city
                if (cities.length > 0) {
                    updateMainAQI(cities[0]);
                }
                
                updateCityList(cities);
                updateParameters(cities[0]); // Use first city's parameters
            }
        } else {
            // Fallback to default cities if no cities in database
            const fallbackCities = [
                { name: 'Delhi', country: 'India', aqi: 152, category: 'Moderate', coordinates: [28.6139, 77.2090] },
                { name: 'Mumbai', country: 'India', aqi: 89, category: 'Moderate', coordinates: [19.0760, 72.8777] },
                { name: 'Chennai', country: 'India', aqi: 67, category: 'Good', coordinates: [13.0827, 80.2707] },
                { name: 'London', country: 'UK', aqi: 45, category: 'Good', coordinates: [51.5074, -0.1278] }
            ];
            
            updateMainAQI(fallbackCities[0]);
            updateCityList(fallbackCities);
            updateParameters(fallbackCities[0]);
        }
        
    } catch (error) {
        console.error('Error loading dashboard data:', error);
        showNotification('Error loading dashboard data. Using fallback data.', 'error');
        
        // Use complete fallback
        const fallbackCities = [
            { name: 'Delhi', country: 'India', aqi: 152, category: 'Moderate', coordinates: [28.6139, 77.2090] },
            { name: 'Mumbai', country: 'India', aqi: 89, category: 'Moderate', coordinates: [19.0760, 72.8777] }
        ];
        updateMainAQI(fallbackCities[0]);
        updateCityList(fallbackCities);
        updateParameters(fallbackCities[0]);
    } finally {
        showLoading(false);
    }
}

// Update main AQI display
function updateMainAQI(cityData) {
    const aqiElement = document.getElementById('mainAqi');
    const categoryElement = document.getElementById('mainCategory');
    const cityElement = document.getElementById('mainCity');
    const progressElement = document.getElementById('aqiProgress');

    aqiElement.textContent = cityData.aqi;
    categoryElement.textContent = cityData.category;
    cityElement.textContent = `${cityData.name}, ${cityData.country}`;

    // Update progress ring
    const circumference = 2 * Math.PI * 90;
    const progress = (cityData.aqi / 300) * circumference;
    progressElement.style.strokeDashoffset = circumference - progress;

    // Update colors based on AQI
    const color = getAQIColor(cityData.aqi);
    aqiElement.className = `aqi-value ${color}`;
    progressElement.style.stroke = getAQIColorValue(cityData.aqi);

    // Animate the value
    animateValue(aqiElement, 0, cityData.aqi, 2000);
}

// Update city list
function updateCityList(cities) {
    const cityListElement = document.getElementById('cityList');
    cityListElement.innerHTML = '';

    cities.forEach((city, index) => {
        const cityCard = document.createElement('div');
        cityCard.className = 'city-card';
        cityCard.style.animationDelay = `${index * 0.1}s`;
        
        cityCard.innerHTML = `
            <div class="city-name">${city.name}, ${city.country}</div>
            <div class="city-aqi ${getAQIColor(city.aqi)}">${city.aqi}</div>
            <div style="color: var(--text-secondary); font-size: 0.9rem;">${city.category}</div>
        `;
        
        cityCard.addEventListener('click', () => selectCity(city));
        cityListElement.appendChild(cityCard);
    });
}

// Update parameters display
function updateParameters(cityData) {
    // If we have city data with pollutant information, use it
    // Otherwise use default parameters
    const parameters = [
        { name: 'PM2.5', value: 45.2, unit: 'µg/m³', color: 'var(--neon-blue)' },
        { name: 'PM10', value: 78.1, unit: 'µg/m³', color: 'var(--neon-green)' },
        { name: 'NO2', value: 32.5, unit: 'µg/m³', color: 'var(--neon-purple)' },
        { name: 'O3', value: 89.3, unit: 'µg/m³', color: '#ffff00' },
        { name: 'SO2', value: 15.7, unit: 'µg/m³', color: '#ff8c00' },
        { name: 'CO', value: 1.2, unit: 'mg/m³', color: '#ff6b6b' }
    ];

    const parametersGrid = document.getElementById('parametersGrid');
    parametersGrid.innerHTML = '';

    parameters.forEach((param, index) => {
        const paramElement = document.createElement('div');
        paramElement.className = 'parameter-item';
        paramElement.style.animationDelay = `${index * 0.1}s`;
        
        paramElement.innerHTML = `
            <div class="parameter-name">${param.name}</div>
            <div class="parameter-value" style="color: ${param.color};">${param.value}</div>
            <div style="font-size: 0.7rem; color: var(--text-secondary);">${param.unit}</div>
        `;
        
        parametersGrid.appendChild(paramElement);
    });
}

// Search city functionality
async function searchCity() {
    const searchInput = document.getElementById('citySearch');
    const cityName = searchInput.value.trim();
    
    if (!cityName) return;

    showLoading(true);
    
    try {
        // First try to search for the city
        const response = await fetch(`${API_BASE_URL}/aqi/search?query=${encodeURIComponent(cityName)}`);
        const data = await response.json();
        
        if (data.success && data.currentData) {
            // Update main display with found city data
            updateMainAQI({
                name: data.currentData.city,
                country: '', // We don't have country info from backend
                aqi: data.currentData.aqiValue,
                category: data.currentData.category
            });
            showNotification(`Data loaded for ${data.currentData.city}!`, 'success');
        } else if (data.success && data.cities && data.cities.length > 0) {
            // If we found matching cities, try to get data for the first one
            const firstCity = data.cities[0];
            const cityResponse = await fetch(`${API_BASE_URL}/aqi/current/${encodeURIComponent(firstCity)}`);
            const cityData = await cityResponse.json();
            
            if (cityData.success) {
                updateMainAQI({
                    name: cityData.data.city,
                    country: '',
                    aqi: cityData.data.aqiValue,
                    category: cityData.data.category
                });
                showNotification(`Data loaded for ${cityData.data.city}!`, 'success');
            }
        } else {
            // Try to add the city to monitoring
            const addResponse = await fetch(`${API_BASE_URL}/aqi/cities/add?city=${encodeURIComponent(cityName)}`, {
                method: 'POST'
            });
            const addData = await addResponse.json();
            
            if (addData.success && addData.data) {
                updateMainAQI({
                    name: addData.data.city,
                    country: '',
                    aqi: addData.data.aqiValue,
                    category: addData.data.category
                });
                showNotification(`New city ${addData.data.city} added to monitoring!`, 'success');
            } else {
                showNotification('City not found or data unavailable. Please try another city.', 'error');
            }
        }
    } catch (error) {
        console.error('Error searching city:', error);
        showNotification('Error searching city. Please try again.', 'error');
    } finally {
        showLoading(false);
        searchInput.value = ''; // Clear search input
    }
}

// Select city from list
async function selectCity(city) {
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/aqi/current/${encodeURIComponent(city.name)}`);
        const data = await response.json();
        
        if (data.success && data.data) {
            updateMainAQI({
                name: data.data.city,
                country: city.country || '',
                aqi: data.data.aqiValue,
                category: data.data.category
            });
            showNotification(`Switched to ${data.data.city}`, 'success');
        } else {
            showNotification(`Unable to load data for ${city.name}`, 'error');
        }
    } catch (error) {
        console.error('Error selecting city:', error);
        showNotification(`Error loading data for ${city.name}`, 'error');
    } finally {
        showLoading(false);
    }
}

// Modal functions
function setAlert() {
    const authHeader = sessionStorage.getItem('authorization');
    if (!authHeader) {
        alert('Please login to set up alerts');
        openModal('loginModal');
        return;
    }
    openModal('alertModal');
}

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.style.display = 'block';
    document.body.style.overflow = 'hidden';
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.style.display = 'none';
    document.body.style.overflow = 'auto';
}

// Login form handler
document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    if (!username || !password) {
        showNotification('Please fill in all fields', 'error');
        return;
    }
    
    try {
        showNotification('Logging in...', 'info');
        
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        const data = await response.json();
        
        if (data.success) {
            currentUser = {
                id: data.userId,
                username: data.username,
                email: data.email,
                city: data.city,
                alertThreshold: data.alertThreshold
            };
            isLoggedIn = true;
            
            // Store user session
            localStorage.setItem('airSightUser', JSON.stringify(currentUser));
            
            // Update UI for logged-in user
            updateNavbarForLoggedInUser();
            closeModal('loginModal');
            showNotification(`Welcome back, ${currentUser.username}!`, 'success');
            
            // Clear form
            document.getElementById('loginForm').reset();
            
            // Load user-specific data
            loadUserAlerts();
            
        } else {
            showNotification(data.message || 'Invalid credentials. Please try again.', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showNotification('Login failed. Please check your connection and try again.', 'error');
    }
});

// Alert form handler
document.getElementById('alertForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    if (!isLoggedIn) {
        showNotification('Please login to set up alerts.', 'error');
        return;
    }
    
    const city = document.getElementById('alertCity').value;
    const threshold = document.getElementById('alertThreshold').value;
    
    try {
        const response = await fetch(`${API_BASE_URL}/alerts/create?city=${encodeURIComponent(city)}&threshold=${threshold}`, {
            method: 'POST',
            headers: {
                'X-User-Id': currentUser.id.toString()
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            closeModal('alertModal');
            showNotification('Alert settings updated successfully!', 'success');
            document.getElementById('alertForm').reset();
            loadUserAlerts();
        } else {
            showNotification(data.message || 'Failed to create alert. Please try again.', 'error');
        }
    } catch (error) {
        console.error('Alert creation error:', error);
        showNotification('Failed to create alert. Please try again.', 'error');
    }
});

// Registration form handler
document.getElementById('registerForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const phoneNumber = document.getElementById('regPhone').value;
    const city = document.getElementById('regCity').value;
    const alertThreshold = document.getElementById('regThreshold').value;
    
    if (!username || !email || !password) {
        showNotification('Please fill in all required fields', 'error');
        return;
    }
    
    try {
        showNotification('Creating account...', 'info');
        
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username,
                email,
                password,
                phoneNumber,
                city,
                alertThreshold: parseInt(alertThreshold)
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            closeModal('registerModal');
            showNotification('Account created successfully! Please login.', 'success');
            document.getElementById('registerForm').reset();
            openModal('loginModal');
        } else {
            showNotification(data.message || 'Registration failed. Please try again.', 'error');
        }
    } catch (error) {
        console.error('Registration error:', error);
        showNotification('Registration failed. Please check your connection and try again.', 'error');
    }
});

// Update navbar for logged-in user
function updateNavbarForLoggedInUser() {
    const loginBtn = document.querySelector('.login-btn');
    loginBtn.innerHTML = `<i class="fas fa-user-circle"></i> ${currentUser.username}`;
    loginBtn.onclick = () => showUserMenu();
}

// Show user menu
function showUserMenu() {
    // Create dropdown menu for logged-in user
    const menu = document.createElement('div');
    menu.className = 'user-menu';
    menu.style.cssText = `
        position: absolute;
        top: 100%;
        right: 0;
        background: var(--card-bg);
        backdrop-filter: blur(20px);
        border: 1px solid var(--glass-border);
        border-radius: 10px;
        padding: 1rem;
        min-width: 200px;
        z-index: 1001;
    `;
    
    menu.innerHTML = `
        <div style="padding: 0.5rem 0; border-bottom: 1px solid var(--glass-border); margin-bottom: 0.5rem;">
            <strong>${currentUser.username}</strong><br>
            <small style="color: var(--text-secondary);">${currentUser.email}</small>
        </div>
        <a href="#" onclick="downloadReport()" style="display: block; padding: 0.5rem 0; color: var(--text-primary); text-decoration: none;">
            <i class="fas fa-download"></i> Download Report
        </a>
        <a href="#" onclick="viewAlerts()" style="display: block; padding: 0.5rem 0; color: var(--text-primary); text-decoration: none;">
            <i class="fas fa-bell"></i> My Alerts
        </a>
        <a href="#" onclick="logout()" style="display: block; padding: 0.5rem 0; color: var(--text-primary); text-decoration: none;">
            <i class="fas fa-sign-out-alt"></i> Logout
        </a>
    `;
    
    // Remove existing menu if any
    const existingMenu = document.querySelector('.user-menu');
    if (existingMenu) {
        existingMenu.remove();
    }
    
    // Add menu to navbar
    const navContainer = document.querySelector('.nav-container');
    navContainer.style.position = 'relative';
    navContainer.appendChild(menu);
    
    // Close menu when clicking outside
    setTimeout(() => {
        document.addEventListener('click', function closeMenu(e) {
            if (!menu.contains(e.target)) {
                menu.remove();
                document.removeEventListener('click', closeMenu);
            }
        });
    }, 100);
}

// View alerts function
function viewAlerts() {
    const userMenu = document.querySelector('.user-menu');
    if (userMenu) {
        userMenu.remove();
    }
    
    // Scroll to alerts section or show modal with alerts
    const alertSection = document.querySelector('.glass-card h3');
    if (alertSection) {
        alertSection.scrollIntoView({ behavior: 'smooth' });
        showNotification('Your alerts are displayed in the panel below', 'info');
    }
}

// Download PDF report
async function downloadReport() {
    if (!isLoggedIn) {
        showNotification('Please login to download reports.', 'error');
        return;
    }
    
    try {
        showNotification('Generating PDF report...', 'info');
        
        const mainCity = document.getElementById('mainCity').textContent.split(',')[0] || 'Delhi';
        
        const response = await fetch(`${API_BASE_URL}/export/pdf?city=${encodeURIComponent(mainCity)}`, {
            method: 'GET',
            headers: {
                'X-User-Id': currentUser.id.toString()
            }
        });
        
        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `air-quality-report-${mainCity}-${new Date().toISOString().split('T')[0]}.pdf`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
            
            showNotification('Report downloaded successfully!', 'success');
        } else {
            const errorText = await response.text();
            showNotification('Failed to generate report: ' + errorText, 'error');
        }
    } catch (error) {
        console.error('Download error:', error);
        showNotification('Failed to download report.', 'error');
    }
}

// Load user alerts
async function loadUserAlerts() {
    if (!isLoggedIn) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/users/alerts`, {
            headers: {
                'X-User-Id': currentUser.id.toString()
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            updateAlertsList(data.alerts);
        } else {
            console.warn('Failed to load user alerts:', data.message);
        }
    } catch (error) {
        console.error('Error loading alerts:', error);
    }
}

// Update alerts list
function updateAlertsList(alerts) {
    const alertList = document.getElementById('alertList');
    alertList.innerHTML = '';
    
    if (alerts.length === 0) {
        alertList.innerHTML = '<p style="color: var(--text-secondary); text-align: center;">No alerts set up yet.</p>';
        return;
    }
    
    alerts.forEach(alert => {
        const alertItem = document.createElement('div');
        alertItem.className = 'alert-item';
        alertItem.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                    <strong>${alert.city}, ${alert.country}</strong><br>
                    <small>Threshold: AQI > ${alert.thresholdValue}</small>
                </div>
                <button onclick="deleteAlert(${alert.id})" style="background: var(--danger-gradient); border: none; border-radius: 5px; padding: 0.5rem; color: white; cursor: pointer;">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        `;
        alertList.appendChild(alertItem);
    });
}

// Delete alert
async function deleteAlert(alertId) {
    try {
        const response = await fetch(`${API_BASE_URL}/alerts/${alertId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${currentUser.token}`
            }
        });
        
        if (response.ok) {
            showNotification('Alert deleted successfully!', 'success');
            loadUserAlerts();
        } else {
            showNotification('Failed to delete alert.', 'error');
        }
    } catch (error) {
        console.error('Delete alert error:', error);
        showNotification('Failed to delete alert.', 'error');
    }
}

// Logout function
function logout() {
    currentUser = null;
    isLoggedIn = false;
    
    // Clear stored session
    localStorage.removeItem('airSightUser');
    
    // Reset navbar
    const loginBtn = document.querySelector('.login-btn');
    loginBtn.innerHTML = '<i class="fas fa-user"></i> Login';
    loginBtn.onclick = () => openModal('loginModal');
    
    // Remove user menu
    const userMenu = document.querySelector('.user-menu');
    if (userMenu) {
        userMenu.remove();
    }
    
    // Reset alert list to default
    const alertList = document.getElementById('alertList');
    alertList.innerHTML = `
        <div class="alert-item">
            <strong>Login Required</strong><br>
            Please login to view personalized alerts<br>
            <small>Register to get SMS notifications</small>
        </div>
    `;
    
    showNotification('Logged out successfully!', 'success');
}

// Utility functions
function getAQIColor(aqi) {
    if (aqi <= 50) return 'aqi-good';
    if (aqi <= 100) return 'aqi-moderate';
    if (aqi <= 150) return 'aqi-unhealthy';
    if (aqi <= 200) return 'aqi-very-unhealthy';
    return 'aqi-hazardous';
}

function getAQIColorValue(aqi) {
    if (aqi <= 50) return 'var(--neon-green)';
    if (aqi <= 100) return '#ffff00';
    if (aqi <= 150) return '#ff8c00';
    if (aqi <= 200) return '#ff0000';
    return '#8b0000';
}

function animateValue(element, start, end, duration) {
    const range = end - start;
    const startTime = performance.now();
    
    function update(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const currentValue = Math.floor(start + range * progress);
        
        element.textContent = currentValue;
        
        if (progress < 1) {
            requestAnimationFrame(update);
        }
    }
    
    requestAnimationFrame(update);
}

function showLoading(show) {
    const loading = document.getElementById('loading');
    const dashboard = document.getElementById('dashboard');
    
    if (show) {
        loading.style.display = 'block';
        dashboard.style.opacity = '0.5';
    } else {
        loading.style.display = 'none';
        dashboard.style.opacity = '1';
    }
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: var(--card-bg);
        backdrop-filter: blur(20px);
        border: 1px solid var(--glass-border);
        border-radius: 10px;
        padding: 1rem 1.5rem;
        color: var(--text-primary);
        z-index: 3000;
        animation: slideInRight 0.3s ease;
        max-width: 300px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    `;
    
    const icon = type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle';
    const color = type === 'success' ? 'var(--neon-green)' : type === 'error' ? '#ff0000' : 'var(--neon-blue)';
    
    notification.innerHTML = `
        <div style="display: flex; align-items: center; gap: 0.5rem;">
            <i class="fas fa-${icon}" style="color: ${color};"></i>
            <span>${message}</span>
        </div>
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// Add slide animations to CSS
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    
    @keyframes slideOutRight {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
`;
document.head.appendChild(style);

// Handle Enter key in search
document.getElementById('citySearch').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        searchCity();
    }
});

// Close modals when clicking outside
window.addEventListener('click', function(e) {
    if (e.target.classList.contains('modal')) {
        e.target.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
});

// Update last updated time
function updateLastUpdatedTime() {
    const now = new Date();
    const lastUpdatedElement = document.getElementById('lastUpdated');
    lastUpdatedElement.textContent = 'Just now';
    
    // Update to relative time after 1 minute
    setTimeout(() => {
        const minutes = Math.floor((new Date() - now) / 60000);
        if (minutes < 1) {
            lastUpdatedElement.textContent = 'Just now';
        } else if (minutes === 1) {
            lastUpdatedElement.textContent = '1 minute ago';
        } else {
            lastUpdatedElement.textContent = `${minutes} minutes ago`;
        }
    }, 60000);
}

// Real-time updates simulation
setInterval(() => {
    // Simulate real-time AQI changes
    const currentAqi = parseInt(document.getElementById('mainAqi').textContent);
    const change = Math.floor(Math.random() * 10) - 5; // Random change between -5 and +5
    const newAqi = Math.max(0, Math.min(500, currentAqi + change));
    
    if (newAqi !== currentAqi) {
        document.getElementById('mainAqi').textContent = newAqi;
        
        // Update color and category
        const color = getAQIColor(newAqi);
        document.getElementById('mainAqi').className = `aqi-value ${color}`;
        
        // Update progress ring
        const progressElement = document.getElementById('aqiProgress');
        const circumference = 2 * Math.PI * 90;
        const progress = (newAqi / 300) * circumference;
        progressElement.style.strokeDashoffset = circumference - progress;
        progressElement.style.stroke = getAQIColorValue(newAqi);
        
        updateLastUpdatedTime();
    }
}, 30000); // Update every 30 seconds

// Initialize with sample data on load
setTimeout(() => {
    updateLastUpdatedTime();
}, 1000);
