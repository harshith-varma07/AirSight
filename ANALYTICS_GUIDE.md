# Analytics Features Usage Guide

This guide explains how to use the new analytics features in AirSight.

## Setup

1. **Install Python Dependencies**
   ```bash
   # Windows
   setup-python-analytics.bat
   
   # Linux/Mac
   bash setup-python-analytics.sh
   ```

2. **Verify Installation**
   - Python 3.8+ installed
   - Required packages: matplotlib, pandas, numpy, reportlab, seaborn

## Using the Analytics Dashboard

### Accessing Analytics
1. Login to your AirSight account
2. Navigate to the Analytics page via the navbar
3. Select a city and date range
4. Click "Load Analytics"

### Available Charts
- **AQI Trend Chart**: Shows air quality changes over time
- **Pollutants Bar Chart**: Average levels of different pollutants
- **AQI Categories Pie Chart**: Distribution of air quality categories
- **Pollution Distribution**: Frequency of different pollution levels

### Export Options
- **Individual Charts**: Click export button on each chart
- **PDF Report**: Download comprehensive report with all charts
- **CSV Data**: Export raw data for external analysis
- **All Charts**: Download all charts as image files

## API Usage

### Get Analytics Statistics
```http
GET /api/export/analytics-stats?city=Delhi&startDate=2025-08-01T00:00:00&endDate=2025-08-20T23:59:59
Authorization: Basic <credentials>
X-User-Id: <user_id>
```

### Generate Enhanced PDF Report
```http
GET /api/export/analytics-pdf?city=Delhi&startDate=2025-08-01T00:00:00&endDate=2025-08-20T23:59:59
Authorization: Basic <credentials>
X-User-Id: <user_id>
```

### Generate Individual Charts
```http
GET /api/export/chart/trend_chart?city=Delhi&startDate=2025-08-01T00:00:00&endDate=2025-08-20T23:59:59
Authorization: Basic <credentials>
X-User-Id: <user_id>
```

Available chart types:
- `trend_chart`: AQI trend over time
- `bar_chart`: Average pollutant levels
- `pie_chart`: AQI category distribution
- `dist_chart`: Pollution level distribution

## Troubleshooting

### Python Not Found
- Ensure Python 3.8+ is installed
- Add Python to system PATH
- Try using `python3` instead of `python`

### Missing Dependencies
- Run `pip install -r python-requirements.txt`
- Check for permission issues
- Use virtual environment if needed

### Charts Not Loading
- Verify user authentication
- Check date range validity
- Ensure sufficient data exists for the period
- Check browser console for errors

### PDF Generation Fails
- Verify Python environment is working
- Check Java can execute Python scripts
- Ensure temporary file permissions
- Check system resources

## File Structure

```
AirSight/
├── frontend/
│   ├── analytics.html      # Analytics dashboard
│   ├── analytics.js        # Analytics functionality
│   └── charts.js          # Chart management
├── python-analytics/
│   └── analytics_service.py # Python analytics service
├── python-requirements.txt  # Python dependencies
└── setup-python-analytics.* # Setup scripts
```
