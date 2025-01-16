interface ButtonParams {
  title: string;
  onClick: () => void;
}

function Button(props: ButtonParams) {
  return (
    <button className="bg-yellow-500 rounded-lg shadow-md container max-w-sm p-6" onClick={props.onClick}>
        <h2 className="text-2xl text-white font-bold text-center">{props.title}</h2>
    </button>
  );
}

export default Button