function fetchVotes() { 
    fetch("getVotes")
        .then(response => response.json())
        .then(data => {
            let html = "";
            data.forEach(candidate => {
                html += `
                    <div style="
                        background-color: #f0f4f8;
                        padding: 10px 15px;
                        border-radius: 8px;
                        margin-bottom: 10px;
                        font-weight: 500;
                        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
                    ">
                        ID: ${candidate.candidate_id} - ${candidate.name} (${candidate.party}) - Votes: ${candidate.votes}
                    </div>`;
            });
            document.getElementById("votesContainer").innerHTML = html;
        });
}

setInterval(fetchVotes, 5000);
window.onload = fetchVotes;
