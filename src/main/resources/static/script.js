/* =========================================================
   SKYCAST — AI WEATHER DASHBOARD
   ========================================================= */


/* ================= ELEMENTS ================= */

const cityInput =
    document.getElementById("city-input");

const searchForm =
    document.getElementById("search-form");

const cityElement =
    document.getElementById("city");

const descriptionElement =
    document.getElementById("description");

const temperatureElement =
    document.getElementById("temperature");

const feelsLikeElement =
    document.getElementById("feels-like");

const humidityElement =
    document.getElementById("humidity");

const windElement =
    document.getElementById("wind");

const pressureElement =
    document.getElementById("pressure");

const weatherIconElement =
    document.getElementById("weather-icon");

const dateElement =
    document.getElementById("date");

const messageElement =
    document.getElementById("message");


/* ================= WEATHER ICON ================= */

function getWeatherIcon(description) {

    const text =
        description.toLowerCase();

    if (
        text.includes("thunder") ||
        text.includes("storm")
    ) {
        return "⛈";
    }

    if (
        text.includes("rain") ||
        text.includes("drizzle")
    ) {
        return "🌧";
    }

    if (
        text.includes("snow")
    ) {
        return "❄";
    }

    if (
        text.includes("cloud")
    ) {
        return "☁";
    }

    if (
        text.includes("mist") ||
        text.includes("fog")
    ) {
        return "≋";
    }

    if (
        text.includes("clear") ||
        text.includes("sun")
    ) {
        return "☀";
    }

    return "◌";
}


/* ================= DATE ================= */

function updateDate() {

    const now =
        new Date();

    dateElement.textContent =
        now.toLocaleDateString(
            "en-IN",
            {
                weekday: "long",
                day: "numeric",
                month: "long",
                year: "numeric"
            }
        ).toUpperCase();
}


/* ================= WEATHER ================= */

async function loadWeather(city) {

    try {

        messageElement.textContent =
            "SYNCING WEATHER DATA...";

        const response =
            await fetch(
                `/api/weather?city=${encodeURIComponent(city)}`
            );

        if (!response.ok) {
            throw new Error(
                "Weather request failed"
            );
        }

        const data =
            await response.json();

        console.log(
            "Weather received:",
            data
        );


        /* CITY */

        cityElement.textContent =
            data.name || city;


        /* TEMPERATURE */

        if (
            data.main &&
            data.main.temp !== undefined
        ) {

            temperatureElement.textContent =
                Math.round(data.main.temp);

        }


        /* FEELS LIKE */

        if (
            data.main &&
            data.main.feels_like !== undefined
        ) {

            feelsLikeElement.textContent =
                `Feels like ${Math.round(
                    data.main.feels_like
                )}°C`;

        }


        /* HUMIDITY */

        if (
            data.main &&
            data.main.humidity !== undefined
        ) {

            humidityElement.textContent =
                `${data.main.humidity}%`;

        }


        /* PRESSURE */

        if (
            data.main &&
            data.main.pressure !== undefined
        ) {

            pressureElement.textContent =
                `${data.main.pressure} hPa`;

        }


        /* WIND */

        if (
            data.wind &&
            data.wind.speed !== undefined
        ) {

            windElement.textContent =
                `${data.wind.speed} m/s`;

        }


        /* DESCRIPTION */

        let description =
            "Weather conditions";

        if (
            data.weather &&
            data.weather.length > 0
        ) {

            description =
                data.weather[0].description;

        }

        descriptionElement.textContent =
            description;


        /* ICON */

        weatherIconElement.textContent =
            getWeatherIcon(description);


        messageElement.textContent =
            "LIVE WEATHER DATA CONNECTED";

    }

    catch (error) {

        console.error(
            "Weather error:",
            error
        );

        messageElement.textContent =
            "Unable to load weather data.";

    }

}


/* ================= SEARCH ================= */

searchForm.addEventListener(
    "submit",
    function (event) {

        event.preventDefault();

        const city =
            cityInput.value.trim();

        if (!city) {
            return;
        }

        loadWeather(city);

    }
);


/* =========================================================
   AI CHAT
   ========================================================= */

const chatForm =
    document.getElementById("chat-form");

const chatInput =
    document.getElementById("chat-input");

const chatMessages =
    document.getElementById("chat-messages");


function addChatMessage(
    text,
    isUser = false
) {

    const message =
        document.createElement("div");

    message.className =
        isUser
            ? "user-message ai-message"
            : "ai-message";


    message.innerHTML = `

        ${
        isUser
            ? ""
            : `<span class="ai-avatar">✦</span>`
    }

        <div>
            ${text}
        </div>

    `;

    chatMessages.appendChild(message);

    chatMessages.scrollTop =
        chatMessages.scrollHeight;
}


chatForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const question =
            chatInput.value.trim();

        if (!question) {
            return;
        }

        addChatMessage(
            question,
            true
        );

        chatInput.value = "";


        try {

            const currentCity =
                cityElement.textContent.trim();

            const response =
                await fetch(
                    `/api/chat?question=${encodeURIComponent(question)}&city=${encodeURIComponent(currentCity)}`
                );

            if (!response.ok) {
                throw new Error(
                    "Chat request failed"
                );
            }

            const answer =
                await response.text();

            addChatMessage(
                answer,
                false
            );

        }
        catch (error) {

            console.error(
                "Chat error:",
                error
            );

            addChatMessage(
                "I couldn't connect to the AI service right now.",
                false
            );

        }

    }
);


/* =========================================================
   GLOBAL WEATHER NEWS
   ========================================================= */

async function loadNews() {

    const newsContainer =
        document.getElementById(
            "newsContainer"
        );

    if (!newsContainer) {
        return;
    }


    newsContainer.innerHTML = `

        <div class="news-empty">
            <p>
                CONNECTING TO GLOBAL WEATHER FEED...
            </p>
        </div>

    `;


    try {

        const response =
            await fetch("/api/news");

        if (!response.ok) {

            throw new Error(
                "News API request failed"
            );

        }

        const data =
            await response.json();

        console.log(
            "News received:",
            data
        );


        displayNews(
            data.articles
        );

    }
    catch (error) {

        console.error(
            "News error:",
            error
        );

        newsContainer.innerHTML = `

            <div class="news-empty">

                <p>
                    Global weather feed temporarily unavailable.
                </p>

            </div>

        `;

    }

}


/* ================= DISPLAY NEWS ================= */

function displayNews(articles) {

    const newsContainer =
        document.getElementById(
            "newsContainer"
        );

    if (!newsContainer) {
        return;
    }


    newsContainer.innerHTML = "";


    if (
        !articles ||
        articles.length === 0
    ) {

        newsContainer.innerHTML = `

            <div class="news-empty">

                <p>
                    No major weather events available right now.
                </p>

            </div>

        `;

        return;
    }


    articles.forEach(
        (article) => {

            const card =
                document.createElement(
                    "article"
                );

            card.className =
                "news-card";


            const title =
                article.title ||
                "Global Weather Update";


            const description =
                article.description ||
                "Latest important weather developments from around the world.";


            const source =
                article.source?.name ||
                "Global Weather";


            const date =
                article.publishedAt
                    ? new Date(
                        article.publishedAt
                    ).toLocaleDateString(
                        "en-IN",
                        {
                            day: "numeric",
                            month: "short",
                            year: "numeric"
                        }
                    )
                    : "";


            const imageUrl =
                article.urlToImage;


            /* IMAGE */

            let imageHTML = "";


            if (imageUrl) {

                imageHTML = `

                    <img
                        src="${imageUrl}"
                        alt="Weather news"
                        class="news-image"
                        loading="lazy"
                        onerror="
                            this.style.display='none';
                            this.nextElementSibling.style.display='flex';
                        "
                    >

                `;

            }


            card.innerHTML = `

                <div class="news-image-wrapper">

                    ${imageHTML}

                    <div
                        class="news-image-placeholder"
                        style="
                            display:
                            ${imageUrl ? "none" : "flex"};
                        "
                    >

                        <span>☁</span>

                        <strong>
                            WEATHER UPDATE
                        </strong>

                        <small>
                            Global Weather News
                        </small>

                    </div>

                </div>


                <div class="news-content">

                    <div class="news-meta">

                        <span>
                            ${escapeHTML(source)}
                        </span>

                        <span>
                            ${date}
                        </span>

                    </div>


                    <h3>
                        ${escapeHTML(title)}
                    </h3>


                    <p>
                        ${escapeHTML(description)}
                    </p>


                    ${
                article.url
                    ? `
                                <a
                                    href="${article.url}"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    class="news-link"
                                >
                                    READ FULL STORY →
                                </a>
                              `
                    : ""
            }

                </div>

            `;


            newsContainer.appendChild(
                card
            );

        }
    );

}


/* ================= HTML SAFETY ================= */

function escapeHTML(value) {

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");

}


/* ================= START ================= */

updateDate();

loadWeather("Mumbai");

loadNews();