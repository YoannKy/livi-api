import { React, ReactDOM } from "https://unpkg.com/es-react/dev";
import htm from "https://cdn.pika.dev/htm";

const html = htm.bind(React.createElement);

const services = [
  { url: "kry.se" },
  { url: "google.se" },
  { url: "www.livi.co.uk" },
];

const ServicePoller = () => {
  const [error, setError] = React.useState(null);

  React.useEffect(() => {
    fetch("/service", {
      headers: {
       Accept: 'application/json',
       'Content-Type': 'application/json'
     },
     mode: 'cors',
    })
      .then((res) => (services = res))
      .catch(setError);
  }, []);

  return html`
    <main>
      <h1>KRY status poller</h1>

      ${error != null &&
        html`
          <div style=${{ color: "red" }}>${error.message}</div>
        `}

      <br />

      <input id="url-input" />

      <a
        href="#"
        onClick=${() => {
          console.log(JSON.stringify({ url: document.getElementById("url-input").value, name: 'test' }))
          fetch("/service", {
            headers: {
             Accept: 'application/json',
             'Content-Type': 'application/json'
           },
            method: 'POST',
            body:{ url: document.getElementById("url-input").value, name: 'test' },
          })
        }}
        style=${{ marginLeft: "1rem" }}
      >
        Save
      </a>

      <ul>
        ${services.map(
          (s) => html`
            <li>${s.url}</li>
          `
        )}
      </ul>
    </main>
  `;
};

ReactDOM.render(React.createElement(ServicePoller), document.body);
